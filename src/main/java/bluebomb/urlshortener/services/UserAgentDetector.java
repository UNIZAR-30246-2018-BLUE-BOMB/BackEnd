package bluebomb.urlshortener.services;

import bluebomb.urlshortener.model.StatsAgent;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detect OS and browser from an user agent
 */
@Service
public class UserAgentDetector {

    private static final String OTHER = "Other";

    private Set<String> supportedOperatingSystems = new HashSet<>(Arrays.asList(
            "Windows",
            "Mac OS X",
            "Linux",
            "Chrome OS",
            "Android",
            "iOS",
            "Symbian OS",
            OTHER
    ));

    /**
     * Get supported operating systems
     *
     * @return supported operating systems
     */
    public List<StatsAgent> getSupportedOS() {
        return Arrays.stream(supportedOperatingSystems.toArray()).map(op -> new StatsAgent((String) op)).collect(Collectors.toList());
    }

    /**
     * Detect user agent OS
     *
     * @param userAgentString user agent obtained from the client
     * @return OS relative to this user agent
     */
    @Cacheable("osDetectorCache")
    public String detectOS(String userAgentString) {
        String operatingSystemName = UserAgent.parseUserAgentString(userAgentString).getOperatingSystem().getGroup().getName();
        return supportedOperatingSystems.contains(operatingSystemName) ? operatingSystemName : OTHER;
    }

    /**
     * Supported browsers
     */
    private Set<String> supportedBrowsers = new HashSet<>(Arrays.asList(
            "Chrome",
            "Firefox",
            "Internet Explorer",
            "Microsoft Edge",
            "Safari",
            "Opera",
            OTHER
    ));

    /**
     * Get supported browsers
     *
     * @return supported browsers
     */
    public List<StatsAgent> getSupportedBrowsers() {
        return Arrays.stream(supportedBrowsers.toArray()).map(op -> new StatsAgent((String) op)).collect(Collectors.toList());
    }

    /**
     * Detect user agent browser
     *
     * @param userAgentString user agent obtained from the client
     * @return OS relative to this user agent
     */
    @Cacheable("browserDetectorCache")
    public String detectBrowser(String userAgentString) {
        String browserName = UserAgent.parseUserAgentString(userAgentString).getBrowser().getGroup().getName();
        return supportedBrowsers.contains(browserName) ? browserName : OTHER;
    }
}
