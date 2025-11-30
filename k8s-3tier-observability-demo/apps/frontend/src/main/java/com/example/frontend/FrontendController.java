package com.example.frontend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FrontendController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/frontend")
    public String frontend() {
        String backendResponse = restTemplate.getForObject("http://backend.demo.svc.cluster.local:9090/backend", String.class);
        return "Frontend received response from backend: " + backendResponse;
    }

    @GetMapping("/health")
    public String health() {
        return "Frontend is healthy";
    }
}
