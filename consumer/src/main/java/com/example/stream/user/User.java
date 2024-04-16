package com.example.stream.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_user")
public class User {
    @Id
    private Long id;
    private String username;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public User() {
    }
    public User(long id, String username) {
        this.id = id;
        this.username = username;
    }

    
}
