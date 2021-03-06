package bluebomb.urlshortener.services;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.RedirectURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Check if an URL or an sequence is reachable
 */
@Service
public class AvailableURIChecker {
    /**
     * Timeout when get petition is done in milliseconds
     */
    private static final int TIMEOUT_GET_PETITION = 1000;

    /**
     * Time between url available check in milliseconds
     */
    private static final int TIME_BETWEEN_URL_AVAILABLE_CHECK = 10000;

    /**
     * Reached URLs list
     */
    private ConcurrentHashMap<String, AtomicBoolean> urlReachedMap = new ConcurrentHashMap<>();

    /**
     * Return true if URL is an available URL (get response status = 200)
     *
     * @param url URL to check
     * @return true if the response status with a GET over URL is 200
     */
    public boolean isURLAvailable(@NonNull String url) {
        // In this function are two cases: The periodic process has already checked the URL and the opposite
        // First case : This function will not perform the GET petition, this one
        // will check the available URL tables created by the periodic process
        // Second case: This function will perform the GET petition
        if (urlReachedMap.containsKey(url))
            return urlReachedMap.get(url).get();
        else
            return getURLResponseStatusFromGet(url) == 200;
    }

    @Autowired
    DatabaseApi databaseApi;

    /**
     * Return true if the original URL identified by id sequence is available
     *
     * @param sequence sequence to check
     * @return true if the response status with a GET over the originURL associated with sequence is 200
     */
    public boolean isSequenceAvailable(@NonNull String sequence) {
        // This function will not perform the GET petition, this will be done by an external periodic process, this one
        // will check the available sequence tables created by this process
        try {
            String url = databaseApi.getHeadURL(sequence);
            if (url != null) {
                boolean isAvailable = isURLAvailable(url);
                if (!urlReachedMap.containsKey(url)) {
                    urlReachedMap.put(url, new AtomicBoolean(isAvailable));
                }
                return isAvailable;
            } else {
                return false;
            }
        } catch (DatabaseInternalException e) {
            return false;
        }
    }

    /**
     * Return true if the Ads associated with the original URL identified by id sequence is available.
     * If there are no Ads associated, return true.
     *
     * @param sequence sequence to check
     * @return true if the response status with a GET over the ad associated with sequence is 200
     */
    public boolean isSequenceAdsAvailable(@NonNull String sequence) {
        // This function will not perform the GET petition, this will be done by an external periodic process, this one
        // will check the available sequence tables created by this process
        // It will only be checked if not be in the table yet
        try {
            RedirectURL adURL = databaseApi.getAd(sequence);
            if (adURL != null) {
                boolean isAvailable = isURLAvailable(adURL.getInterstitialURL());
                if (!urlReachedMap.containsKey(adURL.getInterstitialURL())) {
                    urlReachedMap.put(adURL.getInterstitialURL(), new AtomicBoolean(isAvailable));
                }
                return isAvailable;
            } else {
                return true;
            }
        } catch (DatabaseInternalException e) {
            return false;
        }
    }

    /**
     * Register url to be automatic checked if is reachable.
     *
     * @param url to be registered
     */
    public void registerURL(String url) {
        if (!urlReachedMap.containsKey(url)) {
            urlReachedMap.put(url, new AtomicBoolean(getURLResponseStatusFromGet(url) == 200));
        }
    }

    /**
     * Number of threads to execute available check
     */
    @Value("${app.available-uri-checker-threads:}")
    private long availableUriCheckerThreads;

    /**
     * Check if URLs are reachable in infinite loop
     */
    @Scheduled(fixedRate = TIME_BETWEEN_URL_AVAILABLE_CHECK)
    public void checkIfURLSAreReachableLoop() {
        // Update list response status
        urlReachedMap.forEach(availableUriCheckerThreads, (url, state) ->
                state.set(getURLResponseStatusFromGet(url) == 200)
        );
    }

    /**
     * Return the response status code returned by url after GET petition
     *
     * @param url URL to check status
     * @return response status code, or -1 if something go wrong
     */
    private static int getURLResponseStatusFromGet(@NonNull String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setConnectTimeout(TIMEOUT_GET_PETITION);
            connection.setReadTimeout(TIMEOUT_GET_PETITION);
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
    }
}
