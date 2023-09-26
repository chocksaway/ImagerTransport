package com.chocksaway.service;

import com.chocksaway.reactorflow.dto.ImageDTO;
import com.chocksaway.reactorflow.entities.Image;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

@Service
public class ImageService {

    final Sender sender;

    public ImageService(Sender sender) {
        this.sender = sender;
    }

    public ImageService() {
        this.sender = new Sender();
    }

    // Name of our Queue
    private static final String QUEUE = "image-queue";
    // slf4j logger
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);


    /**
     * Create an image from a DTO
     * @param dto - image dto
     * @return Image
     */
    public Mono<Image> createImage(Mono<ImageDTO> dto) {

        return dto.flatMap(imageDto -> {
            Image image = mapperImageDTOToEntity(imageDto);
            ObjectMapper mapper = new ObjectMapper();
            String json;
            try {
                json = mapper.writeValueAsString(image);
                byte[] imageSerialized = SerializationUtils.serialize(json);
                /*
                 * Simple rabbit queue implementation
                 * Replace with Topic exchange
                 */
                Flux<OutboundMessage> outbound = Flux.just( new OutboundMessage(
                        "",
                        QUEUE, imageSerialized));

                sender.declareQueue(QueueSpecification.queue(QUEUE))
                        .thenMany(sender.sendWithPublishConfirms(outbound))
                        .doOnError(e -> logger.error("Send failed", e))
                        .subscribe(m -> {  // blocking - but need debug
                            System.out.println("Message sent");
                        });
            } catch (JsonProcessingException jpe) {
                logger.warn("JSON processing exception: {}", jpe.toString());
            }

            //Return posted object to the client.
            return Mono.just(image);
        });
    }

    /**
     * Map - dto to image
     * @param dto - Image DTO
     * @return - Image
     */
    public Image mapperImageDTOToEntity(ImageDTO dto) {
        return new Image(dto.item().id(), dto.item().name());
    }

}