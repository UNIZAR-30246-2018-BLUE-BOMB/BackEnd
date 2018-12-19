package bluebomb.urlshortener.services;

import bluebomb.urlshortener.model.StatsAgent;
import eu.bitwalker.useragentutils.UserAgent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Detect OS and Browser from an user agent
 */
public class UserAgentDetection {
    /**
     * Only support static calls
     */
    private UserAgentDetection() {
    }

    private static Set<String> supportedOperatingSystems = new HashSet<>(Arrays.asList(
            "Windows",
            "Mac OS X",
            "Linux",
            "Chrome OS",
            "Android",
            "iOS",
            "Symbian OS",
            "Other"
    ));

    /**
     * Get supported operating systems
     *
     * @return supported operating systems
     */
    public static List<StatsAgent> getSupportedOS() {
        return Arrays.stream(supportedOperatingSystems.toArray()).map(op -> new StatsAgent((String) op)).collect(Collectors.toList());
    }

    /**
     * Detect user agent OS
     *
     * @param userAgentString user agent obtained from the client
     * @return OS relative to this user agent
     */
    public static String detectOS(String userAgentString) {
        String operatingSystemName = UserAgent.parseUserAgentString(userAgentString).getOperatingSystem().getGroup().getName();
        return supportedOperatingSystems.contains(operatingSystemName) ? operatingSystemName : "Other";
    }

    /**
     * Supported browsers
     */
    private static Set<String> supportedBrowsers = new HashSet<>(Arrays.asList(
            "Chrome",
            "Firefox",
            "Internet Explorer",
            "Microsoft Edge",
            "Safari",
            "Opera",
            "Other"
    ));

    /**
     * Get supported browsers
     *
     * @return supported browsers
     */
    public static List<StatsAgent> getSupportedBrowsers() {
        return Arrays.stream(supportedBrowsers.toArray()).map(op -> new StatsAgent((String) op)).collect(Collectors.toList());
    }

    /**
     * Detect user agent browser
     *
     * @param userAgentString user agent obtained from the client
     * @return OS relative to this user agent
     */
    public static String detectBrowser(String userAgentString) {
        String browserName = UserAgent.parseUserAgentString(userAgentString).getBrowser().getGroup().getName();
        return supportedBrowsers.contains(browserName) ? browserName : "Other";
    }
}
