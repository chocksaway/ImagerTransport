

### Imager

I have a fascination about processing events in real-time.  These can be from anything.
The goal of Imager is to react to content events from the Imager CMS (content management system).

One use-case may be to publish a static site from multiple versions of the CMS.  An effective way of routing the correct events to the "correct" Publish(er) is required.

##### ImagerTransport

![imager-overview.png](img%2Fimager-overview.png)

Proof-of-concept implementation of a transport layer for Imager.

RabbitMQ Sender and Receiver basic implementation.

Implemented using a non-blocking reactor based RabbitMQ.

#### Implementation details

Two spring boot applications have been developed.  
They are ReceiverService and SenderService.

The *SenderService* implements a Producer, which uses a RouterFunction to route any Post requests made to "/".  
ImageHandler::createImage is called.  An Image DTO (Data Transfer Object) is used to map to an Image.  
The DTO will allow flexibility as the Image entity changes.

A Json ObjectMapper is used to convert the Image into a byte stream, which is sent to a queue named "image-queue".

The *ReceiverService* implements a Consumer, which includes an ImageReceiverService, using a RabbitMQ Receiver to receive,
and convert (back into) an Image.




