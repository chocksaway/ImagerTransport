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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.util.Objects;

@Service
public class ImageReceiverService {
    private static final String QUEUE = "image-queue";

    @Autowired
    private Mono<Connection> connectionMono;

    private static final Logger logger = LoggerFactory.getLogger(ImageReceiverService.class);

    final Receiver receiver;

    ImageReceiverService(Receiver receiver) {
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

    public Flux<Image> consumeFromQueue() {
        return receiver.consumeAutoAck(QUEUE).mapNotNull(
                each -> {
                    String json = SerializationUtils.deserialize(each.getBody());
                    var mapper = new ObjectMapper();
                    Image image = null;

                    try {
                        image = mapper.readValue(json, Image.class);
                        return image;
                    } catch (JsonProcessingException jpe) {
                        logger.warn("JSON processing exception: {}", jpe.toString());
                    }
                    return image;
                });
    }

    public void consume() {
        receiver.consumeAutoAck(QUEUE).subscribe(m -> {
            String json = SerializationUtils.deserialize(m.getBody());
            var mapper = new ObjectMapper();
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