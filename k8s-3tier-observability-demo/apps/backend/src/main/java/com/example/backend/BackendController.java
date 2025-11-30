package com.example.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    @GetMapping("/backend")
    public String backend() {
        return "Hello from Backend!";
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is healthy";
    }
}
