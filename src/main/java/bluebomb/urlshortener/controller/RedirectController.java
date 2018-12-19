package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.CacheApi;
import bluebomb.urlshortener.errors.ServerInternalError;
import bluebomb.urlshortener.exceptions.CacheInternalException;
import bluebomb.urlshortener.exceptions.DownloadHTMLInternalException;
import bluebomb.urlshortener.model.RedirectURL;
import bluebomb.urlshortener.services.DownloadHTML;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.errors.SequenceNotFoundError;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.services.AvailableURI;

@RestController
public class RedirectController {

    /**
     * Generates ads for specific URL
     *
     * @param sequence Shortened URL sequence code
     */
    @CrossOrigin
    @RequestMapping(value = "{sequence}/ads", produces = MediaType.TEXT_HTML_VALUE)
    public String ads(@PathVariable(value = "sequence") String sequence) {

        try {
            // Check sequence exist
            if (!DatabaseApi.getInstance().containsSequence(sequence)) {
                throw new SequenceNotFoundError();
            }
        } catch (DatabaseInternalException e) {
            // Something go wrong in db
            throw new ServerInternalError();
        }

        RedirectURL adsURL;
        // Get ads url if is in DB
        try {
            adsURL = DatabaseApi.getInstance().getAd(sequence);
        } catch (DatabaseInternalException e) {
            // Something go wrong in db
            throw new ServerInternalError();
        }

        if (adsURL == null) {
            // Sequence has no ads
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sequence have no ads");
        }

        if (!AvailableURI.getInstance().isSequenceAdsAvailable(sequence)) {
            // Associated ads is not available
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated ads is not available");
        }

        String htmlPage = null;

        try {
            //Get the ads web page if it is in cache
            htmlPage = CacheApi.getInstance().getAdHTML(adsURL.getInterstitialURL());
        } catch (CacheInternalException e) {
            // Something go wrong in cache
        }

        if (htmlPage != null) {
            // Ad was in cache
            return htmlPage;
        }

        // If html isn't in cache, download it
        try {
            htmlPage = DownloadHTML.getInstance().download(adsURL.getInterstitialURL());
        } catch (DownloadHTMLInternalException e) {
            // Something go wrong downloading HTML
            throw new ServerInternalError();
        }

        // Save downloaded html in cache
        try {
            CacheApi.getInstance().addAdHTML(adsURL.getInterstitialURL(), htmlPage);
        } catch (CacheInternalException e) {
            // Something go wrong in cache
        }

        return htmlPage;
    }
}