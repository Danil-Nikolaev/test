package com.example.stream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.stream.model.Product;
import com.example.stream.producer.TestKafkaProducer;
import com.example.stream.repository.ProductRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ProductService {
    
    private final int pageSize;
    private final int numThreads;

    private final ProductRepository productRepository;
    private final ExecutorService executor;
    private final TestKafkaProducer testKafkaProducer;
    
    private final List<Processor> pool = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public ProductService(
            ProductRepository productRepository,
            TestKafkaProducer testKafkaProducer,
            @Value("${variable.db.page-size}") int pageSize,
            @Value("${variable.multithreading.num-threads}") int numThreads) {
        
        this.productRepository = productRepository;
        this.pageSize = pageSize;
        this.numThreads = numThreads;
        this.testKafkaProducer = testKafkaProducer;
        this.executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++)
            this.pool.add(new Processor());
    }

    public void unloadDB() throws InterruptedException, ExecutionException {

        LocalDateTime minTime = productRepository.findMinUpdatedAt();
        LocalDateTime maxTime = productRepository.findMaxUpdatedAt();
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

    private class Processor implements Runnable {

        private LocalDateTime startTime;
        private LocalDateTime endTime;

        @Override
        public void run() {

            LocalDateTime lastProcessedTime = startTime;
            Pageable pageable = PageRequest.of(0, pageSize, Sort.by("updatedAt"));

            Slice<Product> productsSlice;
            List<Product> products;
            do {
                productsSlice = productRepository.findByUpdatedAtBetween(lastProcessedTime, endTime, pageable);
                products = productsSlice.getContent();
                if (!products.isEmpty()) {
                    testKafkaProducer.send(products);
                    lastProcessedTime = products.get(products.size() - 1).getUpdatedAt().plusSeconds(1);
                }
            } while (!productsSlice.isEmpty() && lastProcessedTime.isBefore(endTime));

            LOGGER.info("send to kafka all products from partition " + startTime + " to " + endTime);

        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

    }
}
