package bluebomb.urlshortener.service;

import bluebomb.urlshortener.model.StatsAgent;
import bluebomb.urlshortener.services.UserAgentDetector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest

public class UserAgentDetectorTest {
    @Autowired
    UserAgentDetector userAgentDetector;

    @Test
    public void getAllOS() {
        List<StatsAgent> os = userAgentDetector.getSupportedOS();
        assert (os.size() > 1);
        assert os.contains(new StatsAgent("Linux"));
    }

    @Test
    public void getAllBrowsers() {
        List<StatsAgent> browsers = userAgentDetector.getSupportedBrowsers();
        assert (browsers.size() > 1);
        assert browsers.contains(new StatsAgent("Firefox"));
    }

    @Test
    public void detectUserAgent() {
        final String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:63.0) Gecko/20100101 Firefox/63.0";
        assertEquals("Firefox", userAgentDetector.detectBrowser(userAgent));
        assertEquals("Linux", userAgentDetector.detectOS(userAgent));
    }
}
