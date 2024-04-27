package com.example.stream.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductEntityService {

    @Autowired
    private ProductEntityRepository productEntityRepository;

    public void saveProduct(Product product) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(product.getId());
        productEntity.setName(product.getName());
        productEntityRepository.save(productEntity);
    }
}
