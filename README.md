

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
and convert (back into) an Image.  A Get endpoint ("/") is used to consume messages. 

A unit test has been implemented TestReceiverServiceConsumer::testConsumerReceivesImageMessage()
This is currently rather primitive, and needs to be refactored to use a StepVerifier.

![transport.png](img%2Ftransport.png)


#### Running the Sender and Receiver services

(Please follow the instructions in the *Omission and Bug* section at the base of this document).
RabbitMQ needs to be installed and the (image-queue) queue needs to be created manually.

##### Sender Service

    SenderService$ mvn spring-boot:run
    --------------------< com.chocksaway:SenderService >--------------------
    [INFO] Building SenderService 0.0.1-SNAPSHOT
    [INFO] --------------------------------[ jar ]---------------------------------
    main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
    [snip] Started SenderApplication in 0.687 seconds (process running for 0.825)

##### Receiver Service

    ReceiverService$ mvn spring-boot:run
    [INFO] --------------------< com.example:ReceiverService >---------------------
    [INFO] Building ReceiverService 0.0.1-SNAPSHOT
    [INFO] --------------------------------[ jar ]---------------------------------
    [snip]
    [main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8081
    [main] c.c.receiverservice.ReceiverApplication  : Started ReceiverApplication in 0.731 seconds (process running for 0.876)

##### Sending a test message to the Sender Service
    $ curl --header "Content-Type: application/json" --request POST --data '  {"id": 1,"image": {"id" :  1, "name" : "milesd"}}' http://localhost:8080
    {"id":1,"name":"milesd"} $

You will see an acknowledgement:

    {"id":1,"name":"milesd"}

##### Running unit tests

The TestReceiverServiceConsumer test checks for (rabbitMQ) messages received by the ReceiverService.
This test sleeps for 30 seconds, then checks what messages have been received.

The example output uses a "{"id":1,"name":"milesd"}" message.  The test checks (assert true) for this message content.
The test output shows the test being run.  

Whilst the 30 second wait is happening, the curl command (please refer to Sending a test message to the Sender Service) sends messages:

    $ mvn test
    [INFO] Scanning for projects...
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Build Order:
    [INFO]
    [INFO] ImagerTransport                                                    [pom]
    [INFO] ReceiverService                                                    [jar]
    [INFO] SenderService                                                      [jar]
    [INFO] Archetype - Common                                                 [jar]
    [INFO]
    [INFO] -------------------< com.chocksaway:Imagertransport >-------------------
    [INFO] Building ImagerTransport 0.0.1-SNAPSHOT                            [1/4]
    [INFO] --------------------------------[ pom ]---------------------------------
    [INFO]
    [INFO] --------------------< com.example:ReceiverService >---------------------
    [INFO] Building ReceiverService 0.0.1-SNAPSHOT                            [2/4]
    [INFO] --------------------------------[ jar ]---------------------------------
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ ReceiverService ---
    [INFO] Copying 1 resource from src/main/resources to target/classes
    [INFO] Copying 0 resource from src/main/resources to target/classes
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ ReceiverService ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:testResources (default-testResources) @ ReceiverService ---
    [INFO] skip non existing resourceDirectory 
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ ReceiverService ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO]
    [INFO] --- maven-surefire-plugin:3.0.0:test (default-test) @ ReceiverService ---
    [INFO] No tests to run.
    [INFO]
    [INFO] --------------------< com.chocksaway:SenderService >--------------------
    [INFO] Building SenderService 0.0.1-SNAPSHOT                              [3/4]
    [INFO] --------------------------------[ jar ]---------------------------------
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ SenderService ---
    [INFO] Copying 1 resource from src/main/resources to target/classes
    [INFO] Copying 0 resource from src/main/resources to target/classes
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ SenderService ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:testResources (default-testResources) @ SenderService ---
    [INFO] skip non existing resourceDirectory 
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ SenderService ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO]
    [INFO] --- maven-surefire-plugin:3.0.0:test (default-test) @ SenderService ---
    [INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
    [INFO]
    [INFO] -------------------------------------------------------
    [INFO]  T E S T S
    [INFO] -------------------------------------------------------
    [INFO] Running com.chocksaway.reactorflow.mockwebserver.TestImageGetById
    Sept 28, 2023 11:39:13 AM okhttp3.mockwebserver.MockWebServer$start$$inlined$execute$1 run
    INFO: MockWebServer[45623] starting to accept connections
    Sept 28, 2023 11:39:15 AM okhttp3.mockwebserver.MockWebServer acceptConnections
    INFO: MockWebServer[45623] done accepting connections: Socket closed
    [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.56 s - in com.chocksaway.reactorflow.mockwebserver.TestImageGetById
    [INFO] Running com.chocksaway.reactorflow.service.TestReceiverServiceConsumer
    11:39:15.168 [main] INFO Writing to output buffer -- onSubscribe(FluxCreate.BufferAsyncSink)
    11:39:15.170 [main] INFO Writing to output buffer -- request(unbounded)
    11:39:15.173 [main] INFO com.chocksaway.reactorflow.service.TestReceiverServiceConsumer -- Sleeping for 30 seconds
    11:39:18.790 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 47, cap: 2048)))
    11:39:18.790 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 24, cap: 24/24, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 47, cap: 2048)))
    11:39:18.790 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 47, widx: 47, cap: 2048)))
    11:39:19.875 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 47, cap: 1024)))
    11:39:19.876 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 24, cap: 24/24, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 47, cap: 1024)))
    11:39:19.876 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 47, widx: 47, cap: 1024)))
    11:39:20.833 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 47, cap: 1024)))
    11:39:20.833 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 24, cap: 24/24, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 47, cap: 1024)))
    11:39:20.833 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 47, widx: 47, cap: 1024)))
    11:39:21.801 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 47, cap: 512)))
    11:39:21.801 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 24, cap: 24/24, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 47, cap: 512)))
    11:39:21.801 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 47, widx: 47, cap: 512)))
    11:39:22.726 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 47, cap: 512)))
    11:39:22.727 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 24, cap: 24/24, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 47, cap: 512)))
    11:39:22.727 [reactor-http-epoll-3] INFO Writing to output buffer -- onNext(PooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 47, widx: 47, cap: 512)))
    11:39:45.174 [main] INFO com.chocksaway.reactorflow.service.TestReceiverServiceConsumer -- response content:
    data:{"id":1,"name":"milesd"}
    
    
    data:{"id":1,"name":"milesd"}
    
    
    data:{"id":1,"name":"milesd"}
    
    
    data:{"id":1,"name":"milesd"}
    
    
    data:{"id":1,"name":"milesd"}
    
    
    
    [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 30.017 s - in com.chocksaway.reactorflow.service.TestReceiverServiceConsumer
    [INFO]
    [INFO] Results:
    [INFO]
    [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
    [INFO]
    [INFO]
    [INFO] -------------------------< org.example:Common >-------------------------
    [INFO] Building Archetype - Common 0.0.1-SNAPSHOT                         [4/4]
    [INFO] --------------------------------[ jar ]---------------------------------
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ Common ---
    [INFO] skip non existing resourceDirectory 
    [INFO] skip non existing resourceDirectory 
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ Common ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO]
    [INFO] --- maven-resources-plugin:3.3.1:testResources (default-testResources) @ Common ---
    [INFO] skip non existing resourceDirectory 
    [INFO]
    [INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ Common ---
    [INFO] No sources to compile
    [INFO]
    [INFO] --- maven-surefire-plugin:3.0.0:test (default-test) @ Common ---
    [INFO] No tests to run.
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary for ImagerTransport 0.0.1-SNAPSHOT:
    [INFO]
    [INFO] ImagerTransport .................................... SUCCESS [  0.016 s]
    [INFO] ReceiverService .................................... SUCCESS [  0.549 s]
    [INFO] SenderService ...................................... SUCCESS [ 32.339 s]
    [INFO] Archetype - Common ................................. SUCCESS [  0.016 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  33.068 s
    [INFO] Finished at: 2023-09-28T11:39:45+01:00
    [INFO] ------------------------------------------------------------------------
    $

##### Omission and Bug

There is an omission in the README which does not include the installation of RabbitMQ.

The bug is subtle and has only occurred when the producer and consumer are restarted. 
If the consumer starts before the producer, the (image-queue) queue will not be created.  
The solution is to create the queue manually.

I have the chosen the simplest way of creating the queue - by using the rabbitmq management console:

Install the rabbitmq with management docker image:

    $ docker run -d -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
    
Log into the rabbitmq web console:

    Open a web browser to http://localhost:15672/
    Log in with guest guest.
    Navigate to Queues and Streams
    Create a queue named *image-queue*
      Make sure it is not durable (Durability is Transient).
      Click the add queue button.

![rabbitmanagement.png](img%2Frabbitmanagement.png)    
    

    

    






