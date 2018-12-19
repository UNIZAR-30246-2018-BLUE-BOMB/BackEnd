package bluebomb.urlshortener.config;

import java.util.HashSet;
import java.util.Set;

public class CommonValues {

    /**
     * Front end base URL
     */
    public final static String FRONT_END_URI = "http://www.localhost:3000";

    /**
     * Back end base URL
     */
    public final static String BACK_END_URI = "http://www.localhost:8080";

    /**
     * Back end base URL
     */
    public final static String BACK_END_WS_URI = "ws://www.localhost:8080";

    /**
     * Shortened uri prefix
     */
    public final static String FRONT_END_REDIRECT_URI = "http://www.localhost:3000/redirect-page";

    /**
     * Available stats parameters
     */
    public final static Set<String> AVAILABLE_STATS_PARAMETERS;

    static {
        AVAILABLE_STATS_PARAMETERS = new HashSet<>();
        AVAILABLE_STATS_PARAMETERS.add("os");
        AVAILABLE_STATS_PARAMETERS.add("browser");
    }

}
