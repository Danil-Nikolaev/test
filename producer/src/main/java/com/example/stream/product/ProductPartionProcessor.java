package com.example.stream.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public class ProductPartionProcessor implements Runnable {
    private final Long startId;
    private final Long endId;
    private final int pageSize;
    private final ProductRepository productRepository;
    private final StreamBridge streamBridge;
    private final String bindingName;
    private final Logger LOGGER = LoggerFactory.getLogger(ProductPartionProcessor.class);

    public ProductPartionProcessor(Long startId, Long endId, int pageSize, ProductRepository productRepository,
            StreamBridge streamBridge, String bindingName) {
        this.startId = startId;
        this.endId = endId;
        this.pageSize = pageSize;
        this.productRepository = productRepository;
        this.streamBridge = streamBridge;
        this.bindingName = bindingName;
    }

    @Override
    public void run() {
        Long lastProcessedId = startId;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));

        Slice<Product> productsSlice;
        List<Product> products;
        do {
            productsSlice = productRepository.findByIdBetween(lastProcessedId, endId, pageable);
            products = productsSlice.getContent();
            if (!products.isEmpty()) {
                streamBridge.send(bindingName, products);
                lastProcessedId = products.get(products.size() - 1).getId() + 1;
            }
        } while (!productsSlice.isEmpty() && lastProcessedId < endId);

        LOGGER.info("send to kafka all users from partition " + startId + " to " + endId);

    }

}
