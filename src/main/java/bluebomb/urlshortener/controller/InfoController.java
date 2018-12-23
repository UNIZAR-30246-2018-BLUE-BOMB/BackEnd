package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.ShortenedInfoException;
import bluebomb.urlshortener.model.*;
import bluebomb.urlshortener.services.AvailableURIChecker;
import bluebomb.urlshortener.services.UserAgentDetector;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class InfoController {

    private static Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Send original url to subscriber
     *
     * @param sessionId             session if of the user that should receive the original url
     * @param shortenedInfo         original url associated with sequence
     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
     */
    private void sendShortenedInfoToSubscriber(String sessionId, ShortenedInfo shortenedInfo,
                                               SimpMessagingTemplate simpMessagingTemplate) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                "/info/" + shortenedInfo.getSequence(),
                shortenedInfo,
                headerAccessor.getMessageHeaders());
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
                "/queue/error/info",
                new ErrorMessageWS(error),
                headerAccessor.getMessageHeaders());
    }

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-uri:}")
    private String backEndURI;

    /**
     * User agent detector service
     */
    @Autowired
    UserAgentDetector userAgentDetector;

    /**
     * Redirection function to get original URL and statics
     *
     * @param sequence              shortened URL sequence code
     * @param simpSessionId         session id
     * @param simpSessionAttributes attributes
     */
    @SuppressWarnings("unused")
    @MessageMapping("/info")
    public void getShortenedURLInfo(String sequence,
                                    @Header("simpSessionId") String simpSessionId,
                                    @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes
    ) throws ShortenedInfoException, DatabaseInternalException {

        // Get user agent set on interceptor
        String userAgent = (String) simpSessionAttributes.get("user-agent");

        if (!DatabaseApi.getInstance().containsSequence(sequence)) {
            // Unavailable sequence
            throw new ShortenedInfoException("Unavailable sequence: " + sequence, simpSessionId);
        }

        if (!AvailableURIChecker.getInstance().isSequenceAdsAvailable(sequence) || !AvailableURIChecker.getInstance()
                .isSequenceAvailable(sequence)) {
            // Sequence non reachable
            throw new ShortenedInfoException("Sequence non reachable: " + sequence, simpSessionId);
        }

        // Update statics
        String browser = userAgentDetector.detectBrowser(userAgent);
        String os = userAgentDetector.detectOS(userAgent);
        ImmutablePair<Integer, Integer> newStatics = DatabaseApi.getInstance().addStats(sequence, os, browser);

        // Notify new statics to all subscribers
        ClickStat clickStatOS = new ClickStat(os, newStatics.getRight());
        ClickStat clickStatBrowser = new ClickStat(browser, newStatics.getLeft());

        StatsGlobalController.sendStatsToGlobalStatsSubscribers(
                sequence,
                "os",
                new GlobalStats(sequence, "os", clickStatOS),
                simpMessagingTemplate
        );
        StatsGlobalController.sendStatsToGlobalStatsSubscribers(
                sequence,
                "browser",
                new GlobalStats(sequence, "browser", clickStatBrowser),
                simpMessagingTemplate
        );

        // If adds send ad and start thread and if not return url
        RedirectURL ad = DatabaseApi.getInstance().getAd(sequence);
        String originalURL = DatabaseApi.getInstance().getHeadURL(sequence);
        if (ad == null) {
            sendShortenedInfoToSubscriber(simpSessionId,
                    new ShortenedInfo(sequence, originalURL, "", 0),
                    simpMessagingTemplate);
        } else {
            new Thread(() -> {
                // Start a thread that notify user when ad time has end
                try {
                    Thread.sleep(ad.getSecondsToRedirect() * 1000L);
                } catch (Exception e) {
                    // Error when thread try to sleep
                }
                sendShortenedInfoToSubscriber(simpSessionId,
                        new ShortenedInfo(sequence, originalURL, "", 0),
                        simpMessagingTemplate);
            }).start();
            sendShortenedInfoToSubscriber(simpSessionId,
                    new ShortenedInfo(sequence, "", backEndURI + sequence + "/ads", ad.getSecondsToRedirect()),
                    simpMessagingTemplate);
        }
    }

    /**
     * Catch /{sequence}/info produced Exceptions
     *
     * @param e exception captured
     */
    @SuppressWarnings("unused")
    @MessageExceptionHandler({DatabaseInternalException.class, ShortenedInfoException.class})
    public void errorHandlerGetInfo(Exception e) {
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
