package bluebomb.urlshortener.database;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.RedirectURL;
import bluebomb.urlshortener.model.Stats;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseServicesTest {
    @Autowired
    DatabaseApi databaseApi;

    public static final String NOT_EXIST = "notexists";

    @Test
    public void verifygetAdd() throws DatabaseInternalException{
        String sequence = databaseApi.createShortURL("headURL");
        
        assertEquals(databaseApi.getHeadURL(sequence), "headURL");
        assertNull(databaseApi.getAd(sequence));

        assertNull(databaseApi.getAd(NOT_EXIST));

        sequence = databaseApi.createShortURL("shortenedURL", "ad_url");

        RedirectURL red = new RedirectURL(10, "ad_url");
        assertNotNull(databaseApi.getAd(sequence));
        assertEquals(databaseApi.getAd(sequence), red);
    }

    @Test
    public void verifyHeadURL() throws DatabaseInternalException {
        assertNull(databaseApi.getHeadURL(NOT_EXIST));
        assertEquals(databaseApi.getHeadURL("0"), "https://www.google.es/");
    }

    @Test
    public void verifyContainsSequenceTest() throws DatabaseInternalException {
        assertTrue(databaseApi.containsSequence("0"));
        assertFalse(databaseApi.containsSequence(NOT_EXIST));
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

        stats = databaseApi.addStats(NOT_EXIST, "os", "browser");
        assertNull(stats);
    }

    @Test
    public void verifySeqGen(){
        assertEquals(databaseApi.toSequence(0), "");
        assertEquals(databaseApi.toSequence(1), "0");
        assertEquals(databaseApi.toSequence(10), "9");
        assertEquals(databaseApi.toSequence(11), "a");
        assertEquals(databaseApi.toSequence(36), "z");
    }

    @Test
    public void verifyGlobalStats() throws DatabaseInternalException {
        List<ClickStat> globalStats = databaseApi.getGlobalStats(NOT_EXIST, "os");
        assertTrue(globalStats.isEmpty());

        globalStats = databaseApi.getGlobalStats("0", "os");
        assertEquals(2, globalStats.size());
        ClickStat clickStat = new ClickStat("ubuntu", 77);
        assertTrue(globalStats.contains(clickStat));
        clickStat = new ClickStat("windows", 55);
        assertTrue(globalStats.contains(clickStat));
    }

    @Ignore
    @Test
    public void verifyDailyStats() throws DatabaseInternalException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date from = sdf.parse("25/12/2018");
        Date to = sdf.parse("11/01/2019");

        List<Stats> dailyStats = databaseApi.getDailyStats(NOT_EXIST, "os", from, to,"desc", 2);

        assertTrue(dailyStats.isEmpty());

        String sequence = databaseApi.createShortURL("verifyDailyStats");
        databaseApi.addStats(sequence, "ubuntu", "chrome");
        databaseApi.addStats(sequence, "ubuntu", "chrome");
        databaseApi.addStats(sequence, "windows", "chrome");

        dailyStats = databaseApi.getDailyStats(sequence, "os", from, to,"desc", 2);
        assertEquals(1, dailyStats.size());

        List<ClickStat> clickStats = new ArrayList<>();
        ClickStat cs1 = new ClickStat("ubuntu", 2);
        ClickStat cs2 = new ClickStat("windows", 1);
        clickStats.add(cs1);
        clickStats.add(cs2);


        Stats stats = new Stats(to, clickStats);

        assertEquals(stats.getDay(), dailyStats.get(0).getDay());
        assertTrue(dailyStats.get(0).getClickStat().contains(cs1));
        assertTrue(dailyStats.get(0).getClickStat().contains(cs2));

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
        databaseApi.getGlobalStats("0", NOT_EXIST);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStatsNotSupported() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", NOT_EXIST, new Date(), new Date(),
                                "asc", 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStatsNotSupported2() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", "os", new Date(), new Date(),
                NOT_EXIST, 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats() throws DatabaseInternalException {
        databaseApi.getDailyStats(null, "os", new Date(), new Date(),
                                "asc", 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats2() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", null, new Date(), new Date(), 
                                "asc", 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats3() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", "os", null, new Date(),
                                "asc", 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats4() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", "os", new Date(), null,
                                "asc", 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats5() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", "os", new Date(), new Date(),
                                null, 2);
    }

    @Test(expected = DatabaseInternalException.class)
    public void verifyExceptionGetDailyStats6() throws DatabaseInternalException {
        databaseApi.getDailyStats("0", "os", new Date(), new Date(),
                                "asc", null);
    }
}

