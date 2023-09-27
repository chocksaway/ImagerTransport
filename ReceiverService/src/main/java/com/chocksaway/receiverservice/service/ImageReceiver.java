package com.chocksaway.receiverservice.service;

import com.chocksaway.entities.Image;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.publisher.Flux;

interface ImageReceiver {
    String QUEUE = "image-queue";

    @PostConstruct
    void init();

    @PreDestroy
    void close() throws Exception;

    Flux<Image> consumeFromQueue();
}

