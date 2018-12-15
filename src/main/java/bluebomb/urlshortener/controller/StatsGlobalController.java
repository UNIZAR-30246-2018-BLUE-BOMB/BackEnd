package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.config.CommonValues;
import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.ShortenedInfoException;
import bluebomb.urlshortener.exceptions.StatsGlobalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.ErrorMessageWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class StatsGlobalController {

    private static Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    /**
     * Send stats to all globalStats subscribers for some sequence and parameter
     *
     * @param sequence              shortened URL sequence code
     * @param parameter             parameter from which statistics will be obtained
     * @param simpMessagingTemplate SimpMessagingTemplate instance
     * @param stats                 stats to send
     */
    public static void sendStatsToGlobalStatsSubscribers(String sequence, String parameter, ArrayList<ClickStat> stats,
                                                         SimpMessagingTemplate simpMessagingTemplate) {
        simpMessagingTemplate.convertAndSend("/topic/stats/" + parameter + "/global/" + sequence, stats);
    }

    /**
     * Send error to subscriber
     *
     * @param error                 error to send to subscriber
     * @param sessionId             session if of the user that should receive the original url
     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
     */
    private void sendErrorToSubscriber(String sessionId, String error,
                                       SimpMessagingTemplate simpMessagingTemplate) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                "/queue/error/stats/global",
                new ErrorMessageWS(error),
                headerAccessor.getMessageHeaders());
    }


    /**
     * Subscribe to real time global stats
     *
     * @param sequence  shortened URL sequence code
     * @param parameter parameter from which statistics will be obtained
     * @return actual global stats
     */
    @SuppressWarnings("unused")
    @SubscribeMapping("/stats/{parameter}/global/{sequence}")
    public ArrayList<ClickStat> getGlobalStats(@DestinationVariable String sequence,
                                               @DestinationVariable String parameter,
                                               @Header("simpSessionId") String simpSessionId)
            throws StatsGlobalException, DatabaseInternalException {
        if (!CommonValues.AVAILABLE_STATS_PARAMETERS.contains(parameter)) {
            // Unavailable parameter
            throw new StatsGlobalException("Unavailable parameter: " + parameter, simpSessionId);
        }

        if (!DatabaseApi.getInstance().containsSequence(sequence)) {
            // Unavailable sequence
            throw new StatsGlobalException("Unavailable sequence: " + sequence, simpSessionId);
        }
        return DatabaseApi.getInstance().getGlobalStats(sequence, parameter);
    }

    /**
     * Catch getGetGlobalStats produced Exceptions
     *
     * @param e exception captured
     * @return error message
     */
    @SuppressWarnings("unused")
    @MessageExceptionHandler
    public void errorHandlerGetGlobalStats(Exception e) {
        if (e instanceof ShortenedInfoException) {
            // User error
            ShortenedInfoException ex = (ShortenedInfoException) e;
            sendErrorToSubscriber(ex.getUsername(), ex.getMessage(), simpMessagingTemplate);
        } else {
            // Server error
            logger.error(e.getMessage());
        }
    }
}
