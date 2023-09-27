package com.chocksaway.handler;

import com.chocksaway.entities.Image;
import com.chocksaway.reactorflow.dto.ImageDTO;
import com.chocksaway.service.ImageSenderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class ImageHandler {
    private final ImageSenderService service;

    public ImageHandler(ImageSenderService service) {
        this.service = service;
    }

    /**
     *
     * @param request - request from server
     * @return - server response
     */
    public Mono<ServerResponse> createImage(ServerRequest request) {
        var dto = request.bodyToMono(ImageDTO.class);
        var result = service.createImage(dto);

        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, Image.class);
    }
}