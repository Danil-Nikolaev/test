package com.example.stream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/products")
public class Controller {

    @PostMapping
    public Product postMethodName(@RequestBody Product product) {
        
        return product;
    }
    
    
}
