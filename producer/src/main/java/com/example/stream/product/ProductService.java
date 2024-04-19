package com.example.stream.product;

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
        Long lastProcessedId = 0L;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));

        Slice<Product> products;
        do {
            products = productRepository.findByIdGreaterThan(lastProcessedId, pageable);
            for (Product product : products) {
                streamBridge.send(bindingName, product);
                lastProcessedId = product.getId();
            }
        } while (!products.isEmpty());

        // LOGGER.info("send to kafka all users");

    }
}
