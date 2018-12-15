package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.ErrorMessageWS;
import org.junit.Before;
import org.junit.Ignore;
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

    // TODO:
    @Ignore
    @Test
    public void globalStatsEndpoint() throws Exception {
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

    /*    GlobalStatsEndpointStompSessionHandler handler = new GlobalStatsEndpointStompSessionHandler(failure, shortenedSequence, messagesToReceive);

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
        }*/
    }

    @Ignore
    @Test
    public void serverProduceError() throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        final CountDownLatch messagesToReceive = new CountDownLatch(1);

        final String sequence = "secuenciaquenoexiste";

        final String agent = "os";

        StompSessionHandler handler = new GlobalStatsEndpointStompSessionHandler(failure,sequence, agent, messagesToReceive);

        this.stompClient.connect("ws://localhost:{port}/ws", this.headers, handler, this.port);

        if (messagesToReceive.await(10, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                assert failure.get().getMessage().equals("Unavailable sequence");
            } else
                fail("Errors not working");
        } else {
            fail("Response error not received");
        }

    }

    private class GlobalStatsEndpointStompSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;
        private final ClickStatArrayListFrameHandler shortenedInfoFrameHandler;
        private final ErrorFrameHandler errorFrameHandler;
        private final String sequence;
        private final String parameter;
        private final CountDownLatch latch;

        GlobalStatsEndpointStompSessionHandler(AtomicReference<Throwable> failure,
                                               String sequence, String parameter, CountDownLatch latch) {
            this.failure = failure;
            this.sequence = sequence;
            this.parameter = parameter;
            this.latch = latch;
            shortenedInfoFrameHandler = new ClickStatArrayListFrameHandler(latch);
            errorFrameHandler = new ErrorFrameHandler(latch, failure);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            // Subscribe to errors
            session.subscribe("/user/queue/error/stats/global", errorFrameHandler);

            // Subscribe to topic
            session.subscribe("/topic/stats/" + parameter + "/global/" + sequence, shortenedInfoFrameHandler);
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

        ArrayList<ArrayList<ClickStat>> getMessagesCaptured() {
            return shortenedInfoFrameHandler.getMessagesCaptured();
        }

    }

    /**
     * Message handler
     */
    private static class ClickStatArrayListFrameHandler implements StompFrameHandler {
        private CountDownLatch latch;
        private ArrayList<ArrayList<ClickStat>> messagesCaptured = new ArrayList<>();

        ClickStatArrayListFrameHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ArrayList.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            if(payload instanceof ArrayList){
                messagesCaptured.add((ArrayList<ClickStat>) payload);
                latch.countDown();
            }
        }

        ArrayList<ArrayList<ClickStat>> getMessagesCaptured() {
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
            return ErrorMessageWS.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Throwable(((ErrorMessageWS) payload).getError()));
            while (latch.getCount() > 0)
                latch.countDown();
        }
    }

}
