package com.chocksaway.service;

import com.chocksaway.reactorflow.dto.ImageDTO;
import com.rabbitmq.client.AMQP;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

@Service
public class ImageSenderService implements ImageSender {
    final Sender sender;

    public ImageSenderService(Sender sender) {
        this.sender = sender;
    }

    public ImageSenderService() {
        this.sender = new Sender();
    }

    // Name of our Queue
    private static final String QUEUE = "image-queue";

    /*
        Create Queue
     */
    public Mono<AMQP.Queue.DeclareOk> createQueue(Mono<ImageDTO> dto) {
        return sender.declareQueue(QueueSpecification.queue(ImageSenderService.QUEUE));
    }
}