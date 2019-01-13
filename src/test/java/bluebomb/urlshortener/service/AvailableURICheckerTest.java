package bluebomb.urlshortener.service;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.services.AvailableURIChecker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class AvailableURICheckerTest {
    @Autowired
    AvailableURIChecker availableURIChecker;

    @Autowired
    DatabaseApi databaseApi;

    @Test
    public void isURLAvailableTrue() {
        assert availableURIChecker.isURLAvailable("http://www.google.es");
    }

    @Test
    public void isURLAvailableFalse() {
        assert !availableURIChecker.isURLAvailable("dominioquenoexiste");
    }

    @Test
    public void isSequenceAvailableTrue() throws Exception {
        final String headURL = "http://www.google.de";
        // Create shortened URL if not exist
        String shortenedSequence = databaseApi.createShortURL(headURL);
        assert availableURIChecker.isSequenceAvailable(shortenedSequence);
    }

    @Test
    public void isSequenceAvailableFalse() throws Exception {
        final String headURL = "http://urlquenoexiste.uk";
        // Create shortened URL if not exist
        String shortenedSequence = databaseApi.createShortURL(headURL);
        assert !availableURIChecker.isSequenceAvailable(shortenedSequence);
    }

    @Test
    public void isSequenceAvailableNoneExist() {
        assert !availableURIChecker.isSequenceAvailable("Sequenciaquenoexiste");
    }

    @Test
    public void isSequenceAvailableFalseDatabaseException() {
        assert !availableURIChecker.isSequenceAvailable(null);
    }

    @Test
    public void isAdsAvailableTrue() throws Exception {
        final String headURL = "http://www.google.de";
        final String adsURL = "http://www.google.es";
        // Create shortened URL if not exist
        String shortenedSequence = databaseApi.createShortURL(headURL, adsURL);
        assert availableURIChecker.isSequenceAdsAvailable(shortenedSequence);
    }

    @Test
    public void isAdsAvailableFalse() throws Exception {
        final String headURL = "http://www.google.de";
        // Create shortened URL if not exist
        String shortenedSequence = databaseApi.createShortURL(headURL, "http://urlquenoexiste.uk");
        assert !availableURIChecker.isSequenceAdsAvailable(shortenedSequence);
    }

    @Test
    public void isAdsAvailableNoneExist() {
        assert availableURIChecker.isSequenceAdsAvailable("sequenciaquenoexiste");
    }

    @Test
    public void isAdsAvailableFalseDatabaseException() {
        assert !availableURIChecker.isSequenceAdsAvailable(null);
    }

    @Test
    public void automaticAvailableTester() {
        final String headURL = "https://moodle2.unizar.es/add/";

        // A petition from an URL not registered that not exist must be great
        long before = System.currentTimeMillis();
        availableURIChecker.isURLAvailable(headURL);
        long after = System.currentTimeMillis();

        assert (after - before) >= 5;

        availableURIChecker.registerURL(headURL);
        availableURIChecker.checkIfURLSAreReachableLoop();

        // A petition from an URL registered that not exist must be small
        before = System.currentTimeMillis();
        availableURIChecker.isURLAvailable(headURL);
        after = System.currentTimeMillis();
        assert (after - before) < 5;
    }
}
