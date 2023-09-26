package com.chocksaway.receiverservice.service;

import com.chocksaway.receiverservice.entities.Image;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Connection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.util.Objects;

@Service
public class ImageService {
    private static final String QUEUE = "image-queue";

    @Autowired
    private Mono<Connection> connectionMono;

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    final Receiver receiver;

    ImageService(Receiver receiver) {
        this.receiver = receiver;
    }


    @PostConstruct
    private void init()  {
        consume();
    }

    @PreDestroy
    public void close() throws Exception {
        Objects.requireNonNull(connectionMono.block()).close();
    }

    public void consume() {
        receiver.consumeAutoAck(QUEUE).subscribe(m -> {
            String json = SerializationUtils.deserialize(m.getBody());
            ObjectMapper mapper = new ObjectMapper();
            Image image;

            try {
                image = mapper.readValue(json, Image.class);
                System.out.println("----------- RECEIVING -----------------");
                System.out.println(json);
                System.out.println(image.getName());

            } catch (JsonProcessingException jpe) {
                logger.warn("JSON processing exception: {}", jpe.toString());
            }
        });
    }

}