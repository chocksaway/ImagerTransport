package com.chocksaway.receiverservice.controller;

import com.chocksaway.receiverservice.entities.Image;
import com.chocksaway.receiverservice.service.ImageReceiverService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ImageReceiverController {
    @Autowired
    ImageReceiverService imageService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<Image> allMessages() {
        return imageService.consumeFromQueue();
    }
}
