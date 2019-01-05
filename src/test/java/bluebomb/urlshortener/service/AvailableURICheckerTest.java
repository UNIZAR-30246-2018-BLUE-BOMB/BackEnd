package bluebomb.urlshortener.service;

import bluebomb.urlshortener.services.AvailableURIChecker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AvailableURICheckerTest {
    @Autowired
    AvailableURIChecker availableURIChecker;

    @Test
    public void checkResponseStatusTest(){
        assert (availableURIChecker.isURLAvailable("http://www.google.es"));

        availableURIChecker.registerURL("http://www.google.es");

        try {
            Thread.sleep(10000);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        assert (availableURIChecker.isURLAvailable("http://www.google.es"));
    }
}
