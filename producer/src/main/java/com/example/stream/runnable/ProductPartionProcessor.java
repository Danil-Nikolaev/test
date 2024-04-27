package com.example.stream.runnable;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.stream.model.Product;
import com.example.stream.repository.ProductRepository;

public class ProductPartionProcessor implements Runnable {
    private LocalDateTime startTime;
    private LocalDateTime  endTime;
    private final int pageSize;
    private final ProductRepository productRepository;
    private final StreamBridge streamBridge;
    private final String bindingName;
    private final Logger LOGGER = LoggerFactory.getLogger(ProductPartionProcessor.class);

    public ProductPartionProcessor(LocalDateTime startTime, LocalDateTime endTime, int pageSize, ProductRepository productRepository,
            StreamBridge streamBridge, String bindingName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSize = pageSize;
        this.productRepository = productRepository;
        this.streamBridge = streamBridge;
        this.bindingName = bindingName;
    }

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
                streamBridge.send(bindingName, products);
                lastProcessedTime = products.get(products.size() - 1).getUpdatedAt().plusSeconds(1);
            }
        } while (!productsSlice.isEmpty() && lastProcessedTime.isBefore(endTime));

        LOGGER.info("send to kafka all users from partition " + startTime  + " to " + endTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
