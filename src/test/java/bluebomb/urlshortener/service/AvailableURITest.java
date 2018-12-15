package bluebomb.urlshortener.service;

import bluebomb.urlshortener.services.AvailableURI;
import org.junit.Test;

public class AvailableURITest {
    @Test
    public void checkResponseStatusTest(){
        assert (AvailableURI.getInstance().isURLAvailable("http://www.google.es"));

        AvailableURI.getInstance().registerURL("http://www.google.es");

        try {
            Thread.sleep(4000);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        assert (AvailableURI.getInstance().isURLAvailable("http://www.google.es"));
    }
}
