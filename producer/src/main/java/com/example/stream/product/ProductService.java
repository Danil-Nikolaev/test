package com.example.stream.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
 
    @Autowired
    private ProductRepository productRepository;

    // public Slice<Product> getPage(long id) {

    // }

}
