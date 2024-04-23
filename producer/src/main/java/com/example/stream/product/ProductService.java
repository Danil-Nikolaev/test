package com.example.stream.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StreamBridge streamBridge;

    @Value("${variable.kafka.producer.binding-name}")
    private String bindingName;

    @Value("${variable.db.page-size}")
    private int pageSize;

    @Value("${variable.multithreading.num-threads}")
    private int numThreads;

    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public void unloadDB() {

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Long minId = 0L; 
        Long maxId;  
        Long partitionSize; 

        do {
            maxId = productRepository.findMaxId();
            partitionSize = (maxId - minId) / numThreads;

            for (int i = 0; i < numThreads; i++) {
                Long startId = minId + i * partitionSize;
                Long endId = (i == numThreads - 1) ? maxId : startId + partitionSize - 1;

                executor.submit(new ProductPartionProcessor(startId, endId, pageSize, productRepository, streamBridge, bindingName));
            }
            minId = maxId + 1;
        } while (minId <= productRepository.findMaxId());

        executor.shutdown(); 
        while (!executor.isTerminated()) { } 

        LOGGER.info("send to kafka all users");

    }
}
