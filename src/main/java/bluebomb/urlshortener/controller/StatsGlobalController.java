package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.StatsGlobalException;
import bluebomb.urlshortener.errors.WSApiError;
import bluebomb.urlshortener.model.GlobalStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

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
    public static void sendStatsToGlobalStatsSubscribers(String sequence, String parameter, GlobalStats stats,
                                                         SimpMessagingTemplate simpMessagingTemplate) {
        simpMessagingTemplate.convertAndSend("/topic/stats/global/" + parameter + "/" + sequence, stats);
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
                new WSApiError(error),
                headerAccessor.getMessageHeaders());
    }


    /**
     * Request global stats from some parameter and sequence
     *
     * @param sequence  shortened URL sequence code
     * @param parameter parameter from which statistics will be obtained
     * @return actual global stats
     */
    @SuppressWarnings("unused")
    @MessageMapping("/stats/global/{parameter}")
    @SendToUser("/stats/global")
    public GlobalStats getGlobalStats(String sequence,
                                      @DestinationVariable String parameter,
                                      @Header("simpSessionId") String simpSessionId)
            throws StatsGlobalException, DatabaseInternalException {
        if (!(parameter.equals("os") || parameter.equals("browser"))) {
            // Unavailable parameter
            throw new StatsGlobalException("Unavailable parameter: " + parameter, simpSessionId);
        }

        if (!DatabaseApi.getInstance().containsSequence(sequence)) {
            // Unavailable sequence
            throw new StatsGlobalException("Unavailable sequence: " + sequence, simpSessionId);
        }
        return new GlobalStats(sequence, parameter, DatabaseApi.getInstance().getGlobalStats(sequence, parameter));
    }

    /**
     * Catch getGetGlobalStats produced Exceptions
     *
     * @param e exception captured
     */
    @SuppressWarnings("unused")
    @MessageExceptionHandler({StatsGlobalException.class, DatabaseInternalException.class})
    public void errorHandlerGetGlobalStats(Exception e) {
        if (e instanceof StatsGlobalException) {
            // User error
            StatsGlobalException ex = (StatsGlobalException) e;
            sendErrorToSubscriber(ex.getUsername(), ex.getMessage(), simpMessagingTemplate);
        } else {
            // Server error
            logger.error(e.getMessage());
        }
    }
}
