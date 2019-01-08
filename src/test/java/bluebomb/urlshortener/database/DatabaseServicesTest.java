package bluebomb.urlshortener.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.tuple.ImmutablePair;
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
        
        assertEquals(databaseApi.getHeadURL(sequence), "headURL");
        assertNull(databaseApi.getAd(sequence));
    }

    @Test
    public void verifyContainsSequenceTest() throws DatabaseInternalException {
        assertTrue(databaseApi.containsSequence("0"));
        assertFalse(databaseApi.containsSequence("notsequence"));
    }

    @Test
    public void testAddStats() throws DatabaseInternalException {
        String sequence = databaseApi.createShortURL("addStats");

        ImmutablePair<Integer, Integer> stats = databaseApi.addStats(sequence, "os", "browser");

        assertEquals((int) stats.left, 1);
        assertEquals((int) stats.right, 1);

        stats = databaseApi.addStats(sequence, "os", "browser2");

        assertEquals((int) stats.left, 2);
        assertEquals((int) stats.right, 1);

        stats = databaseApi.addStats(sequence, "os2", "browser");

        assertEquals((int) stats.left, 1);
        assertEquals((int) stats.right, 2);
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

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionContainsSequence() throws DatabaseInternalException {
        databaseApi.containsSequence(null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionAddStats() throws DatabaseInternalException {
        databaseApi.addStats(null, "os", "browser");
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionAddStats2() throws DatabaseInternalException {
        databaseApi.addStats("0", null, "browser");
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionAddStats3() throws DatabaseInternalException {
        databaseApi.addStats("0", "os", null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetAdd() throws DatabaseInternalException {
        databaseApi.getAd(null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetHeadURL() throws DatabaseInternalException {
        databaseApi.getHeadURL(null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetGlobalStats() throws DatabaseInternalException {
        databaseApi.getGlobalStats(null, "os");
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetGlobalStats2() throws DatabaseInternalException {
        databaseApi.getGlobalStats("0", null);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetGlobalStatsNotSupported() throws DatabaseInternalException {
        databaseApi.getGlobalStats("0", "notSupported");
    }
}
