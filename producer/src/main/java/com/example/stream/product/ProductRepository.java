package com.example.stream.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>{
    Slice<Product> findByIdGreaterThan(long id, Pageable pageable);
    @Query("SELECT MAX(id) FROM Product")
    Long findMaxId();
    Slice<Product> findByIdBetween(Long lastProcessedId, Long endId, Pageable pageable);
}
