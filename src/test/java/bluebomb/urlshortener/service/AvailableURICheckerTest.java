package bluebomb.urlshortener.service;

import bluebomb.urlshortener.services.AvailableURIChecker;
import org.junit.Test;

public class AvailableURICheckerTest {
    @Test
    public void checkResponseStatusTest(){
        assert (AvailableURIChecker.getInstance().isURLAvailable("http://www.google.es"));

        AvailableURIChecker.getInstance().registerURL("http://www.google.es");

        try {
            Thread.sleep(4000);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        assert (AvailableURIChecker.getInstance().isURLAvailable("http://www.google.es"));
    }
}
