package com.example.stream.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stream.model.DepositDetails;

@Repository
public interface DepositDetailsRepository extends CrudRepository<DepositDetails, UUID>{
    
    @Query("SELECT MAX(d.updateDate) FROM DepositDetails d")
    LocalDateTime findMaxUpdateDate();
    
    @Query("SELECT MIN(d.updateDate) FROM DepositDetails d")
    LocalDateTime findMinUpdateDate();

    Slice<DepositDetails> findByUpdateDateBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}
