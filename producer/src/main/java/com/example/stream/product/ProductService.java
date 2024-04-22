package com.example.stream.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public void unloadDB() {

        int numThreads = 2; // количество потоков
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        Long minId = 0L; // минимальный ID продукта
        Long maxId; // максимальный ID продукта
        Long partitionSize; // размер каждой партиции

        do {
            maxId = productRepository.findMaxId();
            partitionSize = (maxId - minId) / numThreads;

            for (int i = 0; i < numThreads; i++) {
                Long startId = minId + i * partitionSize;
                Long endId = (i == numThreads - 1) ? maxId : startId + partitionSize - 1;

                executor.submit(() -> {
                    Long lastProcessedId = startId;
                    Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));

                    Slice<Product> productsSlice;
                    List<Product> products;
                    do {
                        LOGGER.info("lastProcessedId --- {}", lastProcessedId);
                        LOGGER.info("endId --- {}", endId);
                        productsSlice = productRepository.findByIdBetween(lastProcessedId, endId, pageable);
                        products = productsSlice.getContent();
                        if (!products.isEmpty()) {
                            streamBridge.send(bindingName, products);
                            lastProcessedId = products.get(products.size() - 1).getId() + 1;
                        }
                    } while (!productsSlice.isEmpty() && lastProcessedId < endId);

                    LOGGER.info("send to kafka all users from partition " + startId + " to " + endId);
                });
            }
            try {
                LOGGER.info("sleeep");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            minId = maxId;
        } while (minId < productRepository.findMaxId());

        executor.shutdown(); // останавливаем пул потоков после завершения всех задач
        while (!executor.isTerminated()) {
        } // ждем завершения всех задач

        // Long lastProcessedId = 0L;
        // Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));

        // Slice<Product> productsSlice;
        // List<Product> products;
        // do {
        // productsSlice = productRepository.findByIdGreaterThan(lastProcessedId,
        // pageable);
        // products = productsSlice.getContent();
        // if (!products.isEmpty()) {
        // streamBridge.send(bindingName, products);
        // lastProcessedId = products.get(products.size() - 1).getId();
        // }
        // } while (!productsSlice.isEmpty());

        LOGGER.info("send to kafka all users");

    }
}
