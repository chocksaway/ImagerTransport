package com.chocksaway.reactorflow.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestReceiverServiceConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestReceiverServiceConsumer.class);
    String REQUEST_ENDPOINT = "http://localhost:8081/";

    static WebClient getWebClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        return webClientBuilder.build();
    }


    @Test
    public void testConsumerReceivesImageMessage() throws InterruptedException, IOException {
        WebClient webClient = getWebClient();
        InputStream inputStream = getResponseAsInputStream(webClient, REQUEST_ENDPOINT);
        logger.info("Sleeping for 30 seconds");
        Thread.sleep(30000);
        String content = readContentFromPipedInputStream((PipedInputStream) inputStream);
        logger.info("response content: \n{}", content.replace("}","}\n"));
        assertTrue(content.contains("milesd"));
    }

    public static InputStream getResponseAsInputStream(WebClient client, String url) throws IOException, InterruptedException {

        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 10);
        pipedInputStream.connect(pipedOutputStream);

        Flux<DataBuffer> body = client.get()
                .uri(url)
                .exchangeToFlux(clientResponse -> {
                    return clientResponse.body(BodyExtractors.toDataBuffers());
                })
                .doOnError(error -> {
                    logger.error("error occurred while reading body", error);
                })
                .doFinally(s -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnCancel(() -> {
                    logger.error("Get request is cancelled");
                });

        DataBufferUtils.write(body, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribe();
        return pipedInputStream;
    }

    String readContentFromPipedInputStream(PipedInputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        try {
            Thread pipeReader = new Thread(() -> {
                try {
                    contentStringBuffer.append(readContent(stream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pipeReader.start();
            pipeReader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stream.close();
        }

        return String.valueOf(contentStringBuffer);
    }

    String readContent(InputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        byte[] tmp = new byte[stream.available()];
        int byteCount = stream.read(tmp, 0, tmp.length);
        contentStringBuffer.append(new String(tmp));
        return String.valueOf(contentStringBuffer);
    }
}
