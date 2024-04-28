package com.example.stream.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.stream.projection.ClientBisDetailsProjection;
import com.example.stream.repository.ClientBisDetailsRepository;

@Service
public class ClientBisDetailsService {
    
    @Autowired
    private ClientBisDetailsRepository clientBisDetailsRepository;

    public List<ClientBisDetailsProjection> getClientBisDetailsByIdEvent(UUID idEvent) {
        return clientBisDetailsRepository.findByIdEvent(idEvent);
    }
}
