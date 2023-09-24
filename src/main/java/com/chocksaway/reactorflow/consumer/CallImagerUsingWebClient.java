package com.chocksaway.reactorflow.consumer;

import com.chocksaway.reactorflow.entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    work in progress
 */
public class CallImagerUsingWebClient {
    private static final Logger logger = LoggerFactory.getLogger(CallImagerUsingWebClient.class);
    private static final String baseUrl = "http://localhost:8080";
    private static final WebClient client = WebClient.create(baseUrl);

    public static void main(String[] args) {

        Instant start = Instant.now();

        List<Mono<Image>> list = Stream.of(1, 2)
                .map(i -> client.get().uri("/image/{id}", i).retrieve().bodyToMono(Image.class))
                .collect(Collectors.toList());

        Mono.when(list).log().subscribe();

        logTime(start);
    }

    private static void logTime(Instant start) {
        logger.debug("Elapsed time: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }
}
