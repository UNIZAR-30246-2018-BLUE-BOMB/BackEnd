package bluebomb.urlshortener.model;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest

public class RedirectURLTest {


    @Test
    public void verifyConstructors(){
        RedirectURL redirectURL = new RedirectURL(10, "middle");

        assertNotNull(redirectURL);

        assertEquals(10, (int) redirectURL.getSecondsToRedirect());
        assertEquals("middle", redirectURL.getInterstitialURL());
    }

    @Test
    public void verifyGettersAndSetters(){
        RedirectURL redirectURL = new RedirectURL(10, "middle");

        assertEquals(10, (int) redirectURL.getSecondsToRedirect());
        redirectURL.setSecondsToRedirect(20);
        assertEquals(20, (int) redirectURL.getSecondsToRedirect());

        assertEquals("middle", redirectURL.getInterstitialURL());
        redirectURL.setInterstitialURL("center");
        assertEquals("center", redirectURL.getInterstitialURL());
    }

    @Test
    public void verifyHash(){
        RedirectURL redirectURL = new RedirectURL(10, "middle");
        assertEquals(-1074340212, redirectURL.hashCode());
    }

    @Test
    public void verifyEquals(){
        RedirectURL redirectURL = new RedirectURL(10, "middle");
        RedirectURL redirectURL1 = new RedirectURL(10, "middle");

        assertEquals(redirectURL, redirectURL);
        assertEquals(redirectURL, redirectURL1);
        assertNotEquals(redirectURL, new Object());
        assertNotEquals(redirectURL, null);
    }

    @Test
    public void verifyToString(){
        String regEx = "RedirectURL\\{secondsToRedirect=(\\d+|null),\\sinterstitialURL=\'([a-z]+|null)\'\\}";
        RedirectURL redirectURL = new RedirectURL(10, "middle");
        assertTrue(redirectURL.toString().matches(regEx));

        redirectURL = new RedirectURL(10, null);
        assertTrue(redirectURL.toString().matches(regEx));

        redirectURL = new RedirectURL(null, "middle");
        assertTrue(redirectURL.toString().matches(regEx));
    }
}
