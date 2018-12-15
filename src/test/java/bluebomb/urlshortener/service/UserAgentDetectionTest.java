package bluebomb.urlshortener.service;

import bluebomb.urlshortener.model.StatsAgent;
import bluebomb.urlshortener.services.UserAgentDetection;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserAgentDetectionTest {
    @Test
    public void getAllOS(){
        List<StatsAgent> os = UserAgentDetection.getSupportedOS();
        assert (os.size() > 1);
        assert os.contains(new StatsAgent("Linux"));
    }

    @Test
    public void getAllBrowsers(){
        List<StatsAgent> browsers = UserAgentDetection.getSupportedBrowsers();
        assert (browsers.size() > 1);
        assert browsers.contains(new StatsAgent("Firefox"));
    }

    @Test
    public void detectUserAgent(){
        final String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0";
        assertEquals("Firefox", UserAgentDetection.detectBrowser(userAgent));
        assertEquals("Linux", UserAgentDetection.detectOS(userAgent));
    }
}
