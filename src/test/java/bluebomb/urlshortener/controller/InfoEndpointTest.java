package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ShortenedInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfoEndpointTest {

    @LocalServerPort
    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();


    private final String userAgent = "Morzilla/5.0 (X11; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0";

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.headers.add("User-Agent", userAgent);
    }

    @Test
    public void infoEndpointWithOutAd() throws Exception {
        final String headURL = "http://www.google.de";
        String shortenedSequence = "";
        try {
            // Create shortened URL if not exist
            shortenedSequence = DatabaseApi.getInstance().createShortURL(headURL);
        } catch (DatabaseInternalException e) {
            System.out.println(e.getMessage());
            assert false;
        }

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);
        InfoEndpointStompFrameHandler messageHandler = new InfoEndpointStompFrameHandler(messagesToReceive);

        final String websocketSubscriptionPath = "/ws/" + shortenedSequence + "/info";

        StompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, messageHandler, websocketSubscriptionPath, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
            ArrayList<ShortenedInfo> messagesCaptured = messageHandler.getMessagesCaptured();
            assertEquals(messagesCaptured.size(), 1);
            assertEquals(messagesCaptured.get(0).getHeadURL(), headURL);
            assertEquals(messagesCaptured.get(0).getinterstitialURL(), "");
            assert (messagesCaptured.get(0).getsecondsToRedirect() == 0);
        } else {
            fail("Original URL not received");
        }
    }

    // TODO: No pasa el test ya que el usuario especifico no recibe el segundo mensaje
    @Test
    public void infoEndpointWithAd() throws Exception {
        final String headURL = "http://www.google.de";
        final Integer secondsToRedirect = 10;
        String shortenedSequence = "";

        try {
            // Create shortened URL if not exist
            shortenedSequence = DatabaseApi.getInstance().createShortURL("http://www.google.de", "http://www.unizar.es", secondsToRedirect);
        } catch (DatabaseInternalException e) {
            System.out.println(e.getMessage());
            assert false;
        }

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(2);
        InfoEndpointStompFrameHandler messageHandler = new InfoEndpointStompFrameHandler(messagesToReceive);

        final String websocketSubscriptionPath = "/user/ws/" + shortenedSequence + "/info";

        StompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, messageHandler, websocketSubscriptionPath, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(4, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            } else
                fail("Original URL received too early");
        } else {
            if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
                if (failure.get() != null) {
                    throw new AssertionError("", failure.get());
                }
                ArrayList<ShortenedInfo> messagesCaptured = messageHandler.getMessagesCaptured();
                assertEquals(messagesCaptured.size(), 2);

                assertEquals(messagesCaptured.get(0).getHeadURL(), "");
                System.out.println(messagesCaptured.get(0).getinterstitialURL());
               // assertEquals(messagesCaptured.get(0).getinterstitialURL(), CommonValues.SHORTENED_URI_PREFIX + shortenedSequence + "/ad");
                assertEquals(messagesCaptured.get(0).getsecondsToRedirect(), secondsToRedirect);

                assertEquals(messagesCaptured.get(1).getinterstitialURL(), "");
                assertEquals(messagesCaptured.get(1).getsecondsToRedirect(), new Integer(0));
                assertEquals(messagesCaptured.get(1).getHeadURL(), headURL);
            } else {
                fail("Original URL not received");
            }
        }
    }

    @Test
    public void serverProduceError() throws Exception{
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(2);
        InfoEndpointStompFrameHandler messageHandler = new InfoEndpointStompFrameHandler(messagesToReceive);

        final String websocketSubscriptionPath = "/ws/" + "secuenciaquenoexiste" + "/info";

        StompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, messageHandler, websocketSubscriptionPath, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            } else
                fail("Errors not working");
        } else {
            fail("Response error not received");
        }

    }

    private static class InfoEndpointStompSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;
        private final StompFrameHandler stompFrameHandler;
        private final String subscriptionEndpoint;
        private final CountDownLatch latch;

        public InfoEndpointStompSessionHandler(AtomicReference<Throwable> failure, StompFrameHandler stompFrameHandler,
                                               String subscriptionEndpoint, CountDownLatch latch) {
            this.failure = failure;
            this.stompFrameHandler = stompFrameHandler;
            this.subscriptionEndpoint = subscriptionEndpoint;
            this.latch = latch;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(subscriptionEndpoint, stompFrameHandler);
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
            while (latch.getCount() > 0)
                latch.countDown();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
            while (latch.getCount() > 0)
                latch.countDown();
        }
    }

    /**
     * Message handler
     */
    private static class InfoEndpointStompFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<ShortenedInfo> messagesCaptured = new ArrayList<>();

        public InfoEndpointStompFrameHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ShortenedInfo.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            messagesCaptured.add((ShortenedInfo) payload);
            latch.countDown();
        }

        public ArrayList<ShortenedInfo> getMessagesCaptured() {
            return messagesCaptured;
        }
    }

}
