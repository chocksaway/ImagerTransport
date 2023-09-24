package com.chocksaway.reactorflow.controller;

import com.chocksaway.reactorflow.entities.Image;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {
    private static final List<Image> imageList = new ArrayList<>();
    static {
        imageList.add(new Image(1, "Sunset over Scotland"));
        imageList.add(new Image(2, "East Anglia July afternoon"));
    }

    @GetMapping("/image/{id}")
    public Image getImage(@PathVariable int id, @RequestParam(defaultValue = "1") int delay)
            throws InterruptedException {
        Thread.sleep(delay * 1000);
        return imageList.stream().filter((Image) -> Image.getId() == id).findFirst().get();
    }
}
