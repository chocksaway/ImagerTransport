package com.chocksaway.service;

import com.chocksaway.reactorflow.dto.ImageDTO;
import com.chocksaway.reactorflow.entities.Image;
import reactor.core.publisher.Mono;

interface ImageSender {
    Mono<Image> createImage(Mono<ImageDTO> dto);
}

