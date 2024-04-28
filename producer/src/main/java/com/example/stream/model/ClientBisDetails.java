package com.example.stream.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client_bis_details")
public class ClientBisDetails {
    
    @Column(name = "id_event")
    private UUID idEvent;

    @Column(name = "id_client")
    private String idClient;

    @Column(name = "branch")
    private String branch; 
}
