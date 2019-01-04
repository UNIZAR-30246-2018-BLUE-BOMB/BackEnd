package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.errors.WSApiError;
import bluebomb.urlshortener.model.GlobalStats;
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
public class GlobalStatsEndpointTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

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
    public void globalStatsEndpointInfoFromTopic() throws Exception {
        final String headURL = "http://www.google.de";
        String shortenedSequence = "";
        try {
            // Create shortened URL if not exist
            shortenedSequence = DatabaseApi.getInstance().createShortURL(headURL);
        } catch (DatabaseInternalException e) {
            System.out.println(e.getMessage());
            assert false;
        }

        final String parameter = "os";

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        final String sequence = shortenedSequence;

        final ClickStatArrayListFrameHandler shortenedInfoFrameHandler = new ClickStatArrayListFrameHandler(messagesToReceive);

        GlobalStatsEndpointStompSessionHandler handler = new GlobalStatsEndpointStompSessionHandler(failure,
                messagesToReceive) {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);

                // Subscribe to topic
                session.subscribe("/topic/stats/global/" + parameter + "/" + sequence, shortenedInfoFrameHandler);

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException w){
                    assert false;
                }

                // Update topic stats
                session.send("/app/info", sequence);
            }
        };

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
            ArrayList<GlobalStats> messagesCaptured = shortenedInfoFrameHandler.getMessagesCaptured();
            assertEquals(1, messagesCaptured.size());
            GlobalStats globalStats = messagesCaptured.get(0);
            assertEquals(sequence, globalStats.getSequence());
            assert globalStats.getStats().size()== 1;
        } else {
            fail("Original URL not received");
        }
    }

    @Test
    public void globalStatsEndpointTotalStats() throws Exception {
        final String headURL = "http://www.google.de";
        String shortenedSequence = "";
        try {
            // Create shortened URL if not exist
            shortenedSequence = DatabaseApi.getInstance().createShortURL(headURL);
        } catch (DatabaseInternalException e) {
            System.out.println(e.getMessage());
            assert false;
        }

        final String parameter = "os";

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        final String sequence = shortenedSequence;

        final ClickStatArrayListFrameHandler shortenedInfoFrameHandler = new ClickStatArrayListFrameHandler(messagesToReceive);

        GlobalStatsEndpointStompSessionHandler handler = new GlobalStatsEndpointStompSessionHandler(failure,
                messagesToReceive) {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);

                // Subscribe to topic
                session.subscribe("/user/stats/global" , shortenedInfoFrameHandler);

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException w){
                    assert false;
                }

                // Update topic stats
                session.send("/app/stats/global/" + parameter, sequence);
            }
        };

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
            ArrayList<GlobalStats> messagesCaptured = shortenedInfoFrameHandler.getMessagesCaptured();
            assertEquals(1, messagesCaptured.size());
            GlobalStats globalStats = messagesCaptured.get(0);
            assertEquals(sequence, globalStats.getSequence());
            assert globalStats.getStats().size() >= 1;
        } else {
            fail("Original URL not received");
        }
    }

    @Test
    public void serverProduceError() throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        final String sequence = "secuenciaquenoexiste";
        final String parameter = "os";

        StompSessionHandler handler = new GlobalStatsEndpointStompSessionHandler(failure,  messagesToReceive) {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);

                // Update topic stats
                session.send("/app/stats/global/" + parameter, sequence);
            }
        };

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

    private class GlobalStatsEndpointStompSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;
        private final ErrorFrameHandler errorFrameHandler;
        private final CountDownLatch latch;

        GlobalStatsEndpointStompSessionHandler(AtomicReference<Throwable> failure, CountDownLatch latch) {
            this.failure = failure;
            this.latch = latch;
            errorFrameHandler = new ErrorFrameHandler(latch, failure);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            // Subscribe to errors
            session.subscribe("/user/queue/error/stats/global", errorFrameHandler);

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
    private static class ClickStatArrayListFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<GlobalStats> messagesCaptured = new ArrayList<>();

        ClickStatArrayListFrameHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GlobalStats.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            if (payload instanceof GlobalStats) {
                messagesCaptured.add((GlobalStats) payload);
                latch.countDown();
            }
        }

        ArrayList<GlobalStats> getMessagesCaptured() {
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
