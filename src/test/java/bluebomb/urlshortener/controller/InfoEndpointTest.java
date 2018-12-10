package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.config.CommonValues;
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


    private final String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0";

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

        InfoEndpointStompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, shortenedSequence, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
            ArrayList<ShortenedInfo> messagesCaptured = handler.getMessagesCaptured();
            assertEquals(1, messagesCaptured.size());
            assertEquals(headURL, messagesCaptured.get(0).getHeadURL());
            assertEquals("", messagesCaptured.get(0).getinterstitialURL());
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

        InfoEndpointStompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, shortenedSequence, messagesToReceive);

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
                ArrayList<ShortenedInfo> messagesCaptured = handler.getMessagesCaptured();
                assertEquals(2, messagesCaptured.size());

                assertEquals("", messagesCaptured.get(0).getHeadURL());
                assertEquals(CommonValues.SHORTENED_URI_PREFIX + shortenedSequence + "/ad", messagesCaptured.get(0).getinterstitialURL());
                assertEquals(secondsToRedirect, messagesCaptured.get(0).getsecondsToRedirect());

                assertEquals("", messagesCaptured.get(1).getinterstitialURL());
                assertEquals(new Integer(0), messagesCaptured.get(1).getsecondsToRedirect());
                assertEquals(headURL, messagesCaptured.get(1).getHeadURL());
            } else {
                fail("Original URL not received");
            }
        }
    }

    @Test
    public void serverProduceError() throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        final String sequence = "secuenciaquenoexiste";

        StompSessionHandler handler = new InfoEndpointStompSessionHandler(failure,sequence,  messagesToReceive);

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

    private class InfoEndpointStompSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;
        private final ShortenedInfoFrameHandler shortenedInfoFrameHandler;
        private final ErrorFrameHandler errorFrameHandler;
        private final String sequence;
        private final CountDownLatch latch;

        public InfoEndpointStompSessionHandler(AtomicReference<Throwable> failure,
                                               String subscriptionEndpoint, CountDownLatch latch) {
            this.failure = failure;
            this.sequence = subscriptionEndpoint;
            this.latch = latch;
            shortenedInfoFrameHandler = new ShortenedInfoFrameHandler(latch);
            errorFrameHandler = new ErrorFrameHandler(latch, failure);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            // Subscribe to errors
            session.subscribe("/user/queue/error/" + sequence + "/info", errorFrameHandler);

            // Subscribe to user
          //  session.subscribe("/user/" + sequence + "/info", shortenedInfoFrameHandler);

            // Subscribe to topic
            session.subscribe("/topic/" + sequence + "/info", shortenedInfoFrameHandler);
            System.out.println("Test: Suscrito a " + "/topic/" + sequence + "/info" );

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

        public ArrayList<ShortenedInfo> getMessagesCaptured() {
            return shortenedInfoFrameHandler.getMessagesCaptured();
        }

    }

    /**
     * Message handler
     */
    private static class ShortenedInfoFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<ShortenedInfo> messagesCaptured = new ArrayList<>();

        public ShortenedInfoFrameHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ShortenedInfo.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            System.out.println("Mensaje en handleFrame");
            messagesCaptured.add((ShortenedInfo) payload);
            latch.countDown();
        }

        public ArrayList<ShortenedInfo> getMessagesCaptured() {
            return messagesCaptured;
        }
    }

    /**
     * Error handler
     */
    private static class ErrorFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private AtomicReference<Throwable> failure;

        public ErrorFrameHandler(CountDownLatch latch, AtomicReference<Throwable> failure) {
            this.latch = latch;
            this.failure = failure;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Throwable((String) payload));
            while (latch.getCount() > 0)
                latch.countDown();
        }
    }

}
