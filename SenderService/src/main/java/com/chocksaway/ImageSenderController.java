package com.chocksaway;

import com.chocksaway.handler.ImageHandler;
import com.chocksaway.reactorflow.entities.Image;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageSenderController {
    private static final List<Image> imageList = new ArrayList<>();

    static {
        imageList.add(new Image(1, "Sunset over Scotland"));
        imageList.add(new Image(2, "East Anglia July afternoon"));
    }

    @GetMapping("/image/{id}")
    public Image getImage(@PathVariable int id, @RequestParam(defaultValue = "1") int delay)
            throws InterruptedException {
        Thread.sleep(delay * 1000L);
        return imageList.stream().filter((Image) -> Image.getId() == id).findFirst().get();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ImageHandler handler) {
        return RouterFunctions.route(RequestPredicates.POST("/"), handler::createImage);
    }
}
