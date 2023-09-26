package com.chocksaway.reactorflow.service;

import com.chocksaway.reactorflow.entities.Image;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ImageTestService {
    private final WebClient webClient;

    public ImageTestService(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }
    public Mono<Image> getImageById(Integer employeeId) {
        return webClient
                .get()
                .uri("http://localhost:8080/image/{id}", employeeId)
                .retrieve()
                .bodyToMono(Image.class);
    }
}
