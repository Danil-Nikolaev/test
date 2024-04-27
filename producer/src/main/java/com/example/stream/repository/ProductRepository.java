package com.example.stream.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stream.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>{
    Slice<Product> findByIdGreaterThan(long id, Pageable pageable);

    @Query("SELECT MAX(id) FROM Product")
    Long findMaxId();
    
    Slice<Product> findByIdBetween(Long lastProcessedId, Long endId, Pageable pageable);

    Slice<Product> findByUpdatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    @Query("SELECT MAX(p.updatedAt) FROM Product p")
    LocalDateTime findMaxUpdatedAt();

    @Query("SELECT MIN(p.updatedAt) FROM Product p")
    LocalDateTime findMinUpdatedAt();
}
