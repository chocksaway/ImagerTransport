package com.chocksaway.service;

import com.chocksaway.entities.Image;
import com.chocksaway.reactorflow.dto.ImageDTO;
import reactor.core.publisher.Mono;

interface ImageSender {
    Mono<Image> createImage(Mono<ImageDTO> dto);
}

