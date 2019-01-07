package bluebomb.urlshortener.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class DatabaseServicesTest {
    @Autowired
    DatabaseApi databaseApi;

    @Test
    public void verifyShortUrlNoAdd() throws DatabaseInternalException{
        String sequence = databaseApi.createShortURL("headURL");
        assertEquals(sequence, "2");
        
        assertEquals(databaseApi.getHeadURL(sequence), "headURL");
        assertNull(databaseApi.getAd(sequence));
    }

    // EXCEPTION TESTS

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionShortURL() throws DatabaseInternalException {
        databaseApi.createShortURL(null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionShortURL2() throws DatabaseInternalException {
        databaseApi.createShortURL("test2", null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionShortURL3() throws DatabaseInternalException {
        databaseApi.createShortURL("test2", "test2", null);
    }
}
