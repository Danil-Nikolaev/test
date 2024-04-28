package com.example.stream.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.stream.model.ClientBisDetails;
import com.example.stream.projection.ClientBisDetailsProjection;

@Repository
public interface ClientBisDetailsRepository extends CrudRepository<ClientBisDetails, UUID>{

    List<ClientBisDetailsProjection> findByIdEvent(UUID idEvent);
    
}
