package com.chocksaway.reactorflow.mockwebserver;

import com.chocksaway.reactorflow.entities.Image;
import com.chocksaway.reactorflow.service.ImageTestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

public class TestImageGetById {
    public static MockWebServer mockBackEnd;
    ObjectMapper objectMapper;
    ImageTestService imageService;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        imageService = new ImageTestService(baseUrl);
        objectMapper = new ObjectMapper();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void getImageById() {
        Mono<Image> imageMono = imageService.getImageById(1);

        StepVerifier.create(imageMono)
                .expectNextMatches(image -> image.getName()
                        .equals("Sunset over Scotland"))
                .verifyComplete();
    }

}
