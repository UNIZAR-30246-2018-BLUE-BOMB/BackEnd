package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.config.CommonValues;
import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.ShortenedInfoException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.RedirectURL;
import bluebomb.urlshortener.model.ShortenedInfo;
import bluebomb.urlshortener.services.AvailableURI;
import bluebomb.urlshortener.services.UserAgentDetection;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import java.util.Map;

@Controller
public class InfoController {

    private static Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Send original url to subscriber
     *
     * @param sequence              sequence that user request for
     * @param originalURL           original url associated with sequence
     * @param sessionId             session if of the user that should receive the original url
     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
     */
    public static void sendOriginalUrlToSubscriber(String sessionId, String sequence, String originalURL,
                                                   SimpMessagingTemplate simpMessagingTemplate) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                sequence + "/info",
                new ShortenedInfo(originalURL, "", 0),
                headerAccessor.getMessageHeaders());
    }

    /**
     * Send original url to subscriber
     *
     * @param sequence              sequence that user request for
     * @param error                 error to send to subscriber
     * @param sessionId             session if of the user that should receive the original url
     * @param simpMessagingTemplate a SimpMessagingTemplate instance to perform the call
     */
    public static void sendErrorToSubscriber(String sessionId, String sequence, String error,
                                             SimpMessagingTemplate simpMessagingTemplate) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                "/queue/error/" + sequence + "/info",
                error,
                headerAccessor.getMessageHeaders());
    }

    /**
     * Redirection function to get original URL and statics
     *
     * @param sequence              shortened URL sequence code
     * @param simpSessionId         session id
     * @param simpSessionAttributes attributes
     * @return original URL if no ad and add URL and the time to wait in the other case
     */
    @SubscribeMapping("/{sequence}/info")
    public ShortenedInfo getShortenedURLInfo(@DestinationVariable String sequence,
                                             @Header("simpSessionId") String simpSessionId,
                                             @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes
    ) throws ShortenedInfoException, DatabaseInternalException {

        // Get user agent set on interceptor
        String userAgent = (String) simpSessionAttributes.get("user-agent");

        if (!DatabaseApi.getInstance().containsSequence(sequence)) {
            // Unavailable sequence
            throw new ShortenedInfoException("Unavailable sequence", sequence, simpSessionId);
        }

        if (!AvailableURI.getInstance().isSequenceAdsAvailable(sequence) || !AvailableURI.getInstance()
                .isSequenceAvailable(sequence)) {
            // Sequence non reachable
            throw new ShortenedInfoException("Sequence non reachable", sequence, simpSessionId);
        }

        // Update statics
        String browser = UserAgentDetection.detectBrowser(userAgent);
        String os = UserAgentDetection.detectOS(userAgent);
        ImmutablePair<Integer, Integer> newStatics = DatabaseApi.getInstance().addStats(sequence, os, browser);

        // Notify new statics to all subscribers
        ArrayList<ClickStat> clickStatOS = new ArrayList<>();
        clickStatOS.add(new ClickStat(os, newStatics.getRight()));

        ArrayList<ClickStat> clickStatBrowser = new ArrayList<>();
        clickStatBrowser.add(new ClickStat(browser, newStatics.getLeft()));

        StatsGlobalController.sendStatsToGlobalStatsSubscribers(sequence, "os", clickStatOS, simpMessagingTemplate);
        StatsGlobalController.sendStatsToGlobalStatsSubscribers(sequence, "browser", clickStatBrowser, simpMessagingTemplate);

        // If adds send ad and start thread and if not return url
        RedirectURL ad = DatabaseApi.getInstance().getAd(sequence);
        String originalURL = DatabaseApi.getInstance().getHeadURL(sequence);
        if (ad == null) {
            return new ShortenedInfo(originalURL, "", 0);
        } else {
            new Thread(() -> {
                // Start a thread that notify user when ad time has end
                try {
                    Thread.sleep(ad.getSecondsToRedirect() * 1000L);
                } catch (Exception e) {
                    // Error when thread try to sleep
                }
                InfoController.sendOriginalUrlToSubscriber(sequence, originalURL, simpSessionId, simpMessagingTemplate);
            }).start();
            return new ShortenedInfo("", CommonValues.BACK_END_URI + sequence + "/ads", ad.getSecondsToRedirect());
        }
    }

    /**
     * Catch /{sequence}/info produced Exceptions
     *
     * @param e exception captured
     */
    @MessageExceptionHandler({DatabaseInternalException.class, ShortenedInfoException.class})
    public void errorHandlerGetInfo(Exception e) {
        if (e instanceof ShortenedInfoException) {
            // User error
            ShortenedInfoException ex = (ShortenedInfoException) e;
            sendErrorToSubscriber(ex.getUsername(), ex.getSequence(), ex.getMessage(), simpMessagingTemplate);
        }else {
            // Server error
            logger.error(e.getMessage());
        }
    }
}
