package com.chocksaway.service;

import com.chocksaway.reactorflow.dto.ImageDTO;
import com.rabbitmq.client.AMQP;
import reactor.core.publisher.Mono;

interface ImageSender {
    Mono<AMQP.Queue.DeclareOk> createQueue(Mono<ImageDTO> dto);
}

