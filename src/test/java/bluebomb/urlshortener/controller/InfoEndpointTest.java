package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.errors.WSApiError;
import bluebomb.urlshortener.model.ShortenedInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Autowired
	DatabaseApi databaseApi;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-uri:}")
    private String backEndURI;

    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.headers.add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0");
    }

    @Test
    public void infoEndpointWithOutAd() throws Exception {
        final String headURL = "http://www.google.al";
        String shortenedSequence = "";
        try {
            // Create shortened URL if not exist
            shortenedSequence = databaseApi.createShortURL(headURL);
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
            assertEquals("", messagesCaptured.get(0).getInterstitialURL());
            assert (messagesCaptured.get(0).getSecondsToRedirect() == 0);
        } else {
            fail("Original URL not received");
        }
    }

    @Test
    public void infoEndpointWithAd() throws Exception {
        final String headURL = "http://www.google.al";
        final Integer secondsToRedirect = 10;
        String shortenedSequence = "";

        try {
            // Create shortened URL if not exist
            shortenedSequence = databaseApi.createShortURL("http://www.google.de", "http://www.unizar.es", secondsToRedirect);
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
                assertEquals(backEndURI + "/" + shortenedSequence + "/ads", messagesCaptured.get(0).getInterstitialURL());
                assertEquals(secondsToRedirect, messagesCaptured.get(0).getSecondsToRedirect());

                assertEquals("", messagesCaptured.get(1).getInterstitialURL());
                assertEquals(new Integer(0), messagesCaptured.get(1).getSecondsToRedirect());
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

        StompSessionHandler handler = new InfoEndpointStompSessionHandler(failure, sequence, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                assert failure.get().getMessage().equals("Unavailable sequence: " + sequence);
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

        InfoEndpointStompSessionHandler(AtomicReference<Throwable> failure,
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
            session.subscribe("/user/queue/error/info", errorFrameHandler);

            // Subscribe to user
            session.subscribe("/user/info/" + sequence, shortenedInfoFrameHandler);

            session.send("/app/info", sequence);
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

        ArrayList<ShortenedInfo> getMessagesCaptured() {
            return shortenedInfoFrameHandler.getMessagesCaptured();
        }

    }

    /**
     * Message handler
     */
    private static class ShortenedInfoFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<ShortenedInfo> messagesCaptured = new ArrayList<>();

        ShortenedInfoFrameHandler(CountDownLatch latch) {
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

        ArrayList<ShortenedInfo> getMessagesCaptured() {
            return messagesCaptured;
        }
    }

    /**
     * Error handler
     */
    private static class ErrorFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private AtomicReference<Throwable> failure;

        ErrorFrameHandler(CountDownLatch latch, AtomicReference<Throwable> failure) {
            this.latch = latch;
            this.failure = failure;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return WSApiError.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Throwable(((WSApiError) payload).getError()));
            while (latch.getCount() > 0)
                latch.countDown();
        }
    }

}
