package com.chocksaway.handler;

import com.chocksaway.reactorflow.dto.ImageDTO;
import com.chocksaway.reactorflow.entities.Image;
import com.chocksaway.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class ImageHandler {
    private final ImageService service;

    public ImageHandler(ImageService service) {
        this.service = service;
    }

    /**
     *
     * @param request - request from server
     * @return - server response
     */
    public Mono<ServerResponse> createImage(ServerRequest request) {
        Mono<ImageDTO> dto = request.bodyToMono(ImageDTO.class);
        Mono<Image> result = service.createImage(dto);
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, Image.class);
    }

}