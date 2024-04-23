package com.example.stream.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntityRepository extends CrudRepository<ProductEntity, Long>{
    
}
