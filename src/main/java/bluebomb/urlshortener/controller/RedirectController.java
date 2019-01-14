package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.DownloadHTMLInternalException;
import bluebomb.urlshortener.model.RedirectURL;
import bluebomb.urlshortener.services.AvailableURIChecker;
import bluebomb.urlshortener.services.HTMLDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

@RestController
public class RedirectController {
    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(RedirectController.class);

    /**
     * TTL of the /{sequence}/qr response for the browser cache in seconds
     */
    private static final int BROWSER_GET_AD_TTL = 3600;

    /**
     * HTML downloader service
     */
    @Autowired
    HTMLDownloader htmlDownloader;

    @Autowired
	DatabaseApi databaseApi;

    /**
     * Uri checker service
     */
    @Autowired
    AvailableURIChecker availableURIChecker;

    /**
     * Generates ads for specific URL
     *
     * @param sequence Shortened URL sequence code
     */
    @CrossOrigin
    @RequestMapping(value = "{sequence}/ads", produces = {MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<String> ads(@PathVariable(value = "sequence") String sequence) throws DatabaseInternalException, DownloadHTMLInternalException {
        // Check sequence exist
        if (!databaseApi.containsSequence(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not available");

        }

        // Get ads url if is in DB
        RedirectURL adsURL = databaseApi.getAd(sequence);


        if (adsURL == null) {
            // Sequence has no ads
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sequence have no ads");
        }

        if (!availableURIChecker.isSequenceAdsAvailable(sequence)) {
            // Associated ads is not available
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated ads is not available");
        }

        // Download the ad page
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(BROWSER_GET_AD_TTL, TimeUnit.SECONDS))
                .body(htmlDownloader.download(adsURL.getInterstitialURL()));
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR,
            reason="Internal server error")
    @ExceptionHandler({DatabaseInternalException.class, DownloadHTMLInternalException.class})
    public void exceptionHandlerInternalServerError(Exception e){
        logger.error(e.getMessage());
    }
}