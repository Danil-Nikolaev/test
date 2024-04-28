package com.example.stream.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.stream.model.DepositDetails;
import com.example.stream.producer.DepositDetailsProducer;
import com.example.stream.repository.DepositDetailsRepository;

@Service
public class DepositDetailsService {
    
    private final DepositDetailsRepository depositDetailsRepository;
    private final int pageSize;
    private final int numThreads;
    private final ExecutorService executor;
    private final DepositDetailsProducer depositDetailsProducer;
    private final List<Processor> pool = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(DepositDetailsService.class);
    
    public DepositDetailsService(
            DepositDetailsRepository depositDetailsRepository, 
            @Value("${variable.db.page-size}") int pageSize, 
            @Value("${variable.multhithreading.num-threads}") int numThreads,
            ExecutorService executor, 
            DepositDetailsProducer depositDetailsProducer) {
        this.depositDetailsRepository = depositDetailsRepository;
        this.pageSize = pageSize;
        this.numThreads = numThreads;
        this.executor = executor;
        this.depositDetailsProducer = depositDetailsProducer;
        
        for (int i = 0; i < numThreads; i++) 
                pool.add(new Processor());
    }
    
    public void unloadDB() throws InterruptedException, ExecutionException {
       
        LocalDateTime minTime = depositDetailsRepository.findMinUpdateDate();
        LocalDateTime maxTime = depositDetailsRepository.findMaxUpdateDate();
        Duration partitionDuration = Duration.between(minTime, maxTime).dividedBy(numThreads);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            LocalDateTime startTime = minTime.plus(partitionDuration.multipliedBy(i));
            LocalDateTime endTime = (i == numThreads - 1) ? maxTime : startTime.plus(partitionDuration);

            Processor processor = pool.get(i);
            processor.setStartTime(startTime);
            processor.setEndTime(endTime);

            Future<?> future = executor.submit(processor);
            futures.add(future);
        }

        for (Future<?> future : futures)
            future.get();

        LOGGER.info("send to kafka all products");

    }

    private class Processor implements Runnable{

        private LocalDateTime startTime;
        private LocalDateTime endTime;

        @Override
        public void run() {
        
            LocalDateTime lastProcessedTime = startTime;
            Pageable pageable = PageRequest.of(0, pageSize, Sort.by("updatedAt"));

            Slice<DepositDetails> productsSlice;
            List<DepositDetails> products;
            do {
                productsSlice = depositDetailsRepository.findByUpdateDateBetween(lastProcessedTime, endTime, pageable);
                products = productsSlice.getContent();
                if (!products.isEmpty()) {
                    lastProcessedTime = products.get(products.size() - 1).getUpdateDate().plusSeconds(1);
                }
            } while (!productsSlice.isEmpty() && lastProcessedTime.isBefore(endTime));

            LOGGER.info("send to kafka all products from partition " + startTime + " to " + endTime);
   
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
    }
}
