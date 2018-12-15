package bluebomb.urlshortener.services;

import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.RedirectURL;
import org.springframework.lang.NonNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Check if an URL or an sequence is reachable
 */
public class AvailableURI {
    /**
     * Timeout when get petition is done in milliseconds
     */
    private static final int TIMEOUT_GET_PETITION = 1000;

    /**
     * Time between url available check in milliseconds
     */
    private static final int TIME_BETWEEN_URL_AVAILABLE_CHECK = 1000;

    /**
     * Reached URLs list
     */
    private ConcurrentHashMap<String, AtomicBoolean> urlReachedMap = new ConcurrentHashMap<>();

    /**
     * Thread that check if all URLs are reachable
     */
    private Thread availableURLCheckerThread;

    /**
     * Instance
     */
    private static AvailableURI ourInstance = new AvailableURI();

    /**
     * Get an instance of the class (Singleton pattern)
     *
     * @return the instance of the class
     */
    public static AvailableURI getInstance() {
        return ourInstance;
    }

    /**
     * Init background thread
     */
    private AvailableURI() {
        availableURLCheckerThread = new Thread(this::checkIfURLSAreReachableLoop);
        availableURLCheckerThread.start();

    }

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
            String url = DatabaseApi.getInstance().getHeadURL(sequence);
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
            RedirectURL adURL = DatabaseApi.getInstance().getAd(sequence);
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
     * Register url in the list.
     *
     * @param url to be registered
     */
    public void registerURL(String url) {
        if (!urlReachedMap.containsKey(url)) {
            urlReachedMap.put(url, new AtomicBoolean(getURLResponseStatusFromGet(url) == 200));
        }
    }

    /**
     * Check if URLs are reachable in infinite loop
     */
    private void checkIfURLSAreReachableLoop() {
        while (true) {
            // Update list response status
            urlReachedMap.forEach((url, state) ->
                    state.set(getURLResponseStatusFromGet(url) == 200)
            );

            try {
                // Avoid too much cpu consumption in this process
                // The probability of a fall of the site in the last 5 seconds is low
                Thread.sleep(TIME_BETWEEN_URL_AVAILABLE_CHECK);
            } catch (InterruptedException e) {
                // Process cannot sleep
            }
        }
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
