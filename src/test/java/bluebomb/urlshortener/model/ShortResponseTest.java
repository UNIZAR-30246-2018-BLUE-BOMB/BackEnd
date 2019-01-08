package bluebomb.urlshortener.model;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class ShortResponseTest {


    @Test
    public void verifyEmptyConstructor(){
        ShortResponse shortResponse = new ShortResponse();

        assertNotNull(shortResponse);
        assertNull(shortResponse.getSequence());
        assertNull(shortResponse.getShortedUrl());
        assertNull(shortResponse.getQrReferenceUrl());
        assertNull(shortResponse.getInfoUrlRequestChannel());
        assertNull(shortResponse.getInfoUrlListenChannel());
        assertNull(shortResponse.getInfoUrlErrorChannel());
        assertNull(shortResponse.getDailyStatsOperatingSystemUrl());
        assertNull(shortResponse.getDailyStatsBrowserUrl());
        assertNull(shortResponse.getGlobalStatsRequestOperatingSystemChannel());
        assertNull(shortResponse.getGlobalStatsRequestBrowserChannel());
        assertNull(shortResponse.getGlobalStatsListenChannel());
        assertNull(shortResponse.getGlobalStatsErrorChannel());
        assertNull(shortResponse.getGlobalStatsOperatingSystemChangesListenChannel());
        assertNull(shortResponse.getGlobalStatsBrowserChangesListenChannel());
        assertNull(shortResponse.getAdsUrl());
    }

    @Test
    public void verifyConstructor(){
        ShortResponse shortResponse = new ShortResponse("0", false, "frontEndRedirectURI", "backEndURI", "backEndWsURI");

        String sequence = shortResponse.getSequence();
        assertEquals("0", sequence);
        String shortedUrl = shortResponse.getShortedUrl();
        assertEquals("frontEndRedirectURI/0", shortedUrl);
        String qrReferenceUrl = shortResponse.getQrReferenceUrl();
        assertEquals("backEndURI/0/qr", qrReferenceUrl);
        String infoUrlRequestChannel = shortResponse.getInfoUrlRequestChannel();
        assertEquals("backEndWsURI/app/info", infoUrlRequestChannel);
        String infoUrlListenChannel = shortResponse.getInfoUrlListenChannel();
        assertEquals("backEndWsURI/user/info/0", infoUrlListenChannel);
        String infoUrlErrorChannel = shortResponse.getInfoUrlErrorChannel();
        assertEquals("backEndWsURI/user/queue/error/info", infoUrlErrorChannel);
        String dailyStatsOperatingSystemUrl = shortResponse.getDailyStatsOperatingSystemUrl();
        assertEquals("backEndURI/0/stats/os/daily", dailyStatsOperatingSystemUrl);
        String dailyStatsBrowserUrl = shortResponse.getDailyStatsBrowserUrl();
        assertEquals("backEndURI/0/stats/browser/daily", dailyStatsBrowserUrl);
        String globalStatsRequestOperatingSystemChannel = shortResponse.getGlobalStatsRequestOperatingSystemChannel();
        assertEquals("backEndWsURI/app/stats/global/os", globalStatsRequestOperatingSystemChannel);
        String globalStatsRequestBrowserChannel = shortResponse.getGlobalStatsRequestBrowserChannel();
        assertEquals("backEndWsURI/app/stats/global/browser", globalStatsRequestBrowserChannel);
        String globalStatsListenChannel = shortResponse.getGlobalStatsListenChannel();
        assertEquals("backEndWsURI/user/stats/global", globalStatsListenChannel);
        String globalStatsErrorChannel = shortResponse.getGlobalStatsErrorChannel();
        assertEquals("backEndWsURI/user/queue/error/stats/global", globalStatsErrorChannel);
        String globalStatsOperatingSystemChangesListenChannel = shortResponse.getGlobalStatsOperatingSystemChangesListenChannel();
        assertEquals("backEndWsURI/topic/stats/global/os/0", globalStatsOperatingSystemChangesListenChannel);
        String globalStatsBrowserChangesListenChannel = shortResponse.getGlobalStatsBrowserChangesListenChannel();
        assertEquals("backEndWsURI/topic/stats/global/browser/0", globalStatsBrowserChangesListenChannel);
        String adsUrl = shortResponse.getAdsUrl();
        assertEquals("", adsUrl);

        shortResponse = new ShortResponse("0", true, "frontEndRedirectURI", "backEndURI", "backEndWsURI");
        adsUrl = shortResponse.getAdsUrl();
        assertEquals("backEndURI/" + sequence + "/ads", adsUrl);



        ShortResponse shortResponse1 = new ShortResponse(sequence, shortedUrl, qrReferenceUrl, infoUrlRequestChannel,
                infoUrlListenChannel, infoUrlErrorChannel, dailyStatsOperatingSystemUrl,
                dailyStatsBrowserUrl, globalStatsRequestOperatingSystemChannel,
                globalStatsRequestBrowserChannel, globalStatsListenChannel,
                globalStatsErrorChannel, globalStatsOperatingSystemChangesListenChannel,
                globalStatsBrowserChangesListenChannel, adsUrl);
        assertEquals(shortResponse, shortResponse1);

    }

    @Test
    public void verifyGettersAndSetters(){
        ShortResponse shortResponse = new ShortResponse();

        assertNull(shortResponse.getSequence());
        shortResponse.setSequence("1");
        assertEquals("1", shortResponse.getSequence());

        assertNull(shortResponse.getShortedUrl());
        shortResponse.setShortedUrl("shortedUrl");
        assertEquals("shortedUrl", shortResponse.getShortedUrl());

        assertNull(shortResponse.getQrReferenceUrl());
        shortResponse.setQrReferenceUrl("qrReferenceUrl");
        assertEquals("qrReferenceUrl", shortResponse.getQrReferenceUrl());

        assertNull(shortResponse.getInfoUrlRequestChannel());
        shortResponse.setInfoUrlRequestChannel("infoUrlRequestChannel");
        assertEquals("infoUrlRequestChannel", shortResponse.getInfoUrlRequestChannel());

        assertNull(shortResponse.getInfoUrlListenChannel());
        shortResponse.setInfoUrlListenChannel("infoUrlListenChannel");
        assertEquals("infoUrlListenChannel", shortResponse.getInfoUrlListenChannel());

        assertNull(shortResponse.getInfoUrlErrorChannel());
        shortResponse.setInfoUrlErrorChannel("infoUrlErrorChannel");
        assertEquals("infoUrlErrorChannel", shortResponse.getInfoUrlErrorChannel());

        assertNull(shortResponse.getDailyStatsOperatingSystemUrl());
        shortResponse.setDailyStatsOperatingSystemUrl("dailyStatsOperatingSystemUrl");
        assertEquals("dailyStatsOperatingSystemUrl", shortResponse.getDailyStatsOperatingSystemUrl());

        assertNull(shortResponse.getDailyStatsBrowserUrl());
        shortResponse.setDailyStatsBrowserUrl("dailyStatsBrowserUrl");
        assertEquals("dailyStatsBrowserUrl", shortResponse.getDailyStatsBrowserUrl());

        assertNull(shortResponse.getGlobalStatsRequestOperatingSystemChannel());
        shortResponse.setGlobalStatsRequestOperatingSystemChannel("globalStatsRequestOperatingSystemChannel");
        assertEquals("globalStatsRequestOperatingSystemChannel", shortResponse.getGlobalStatsRequestOperatingSystemChannel());

        assertNull(shortResponse.getGlobalStatsRequestBrowserChannel());
        shortResponse.setGlobalStatsRequestBrowserChannel("globalStatsRequestBrowserChannel");
        assertEquals("globalStatsRequestBrowserChannel", shortResponse.getGlobalStatsRequestBrowserChannel());

        assertNull(shortResponse.getGlobalStatsListenChannel());
        shortResponse.setGlobalStatsListenChannel("globalStatsListenChannel");
        assertEquals("globalStatsListenChannel", shortResponse.getGlobalStatsListenChannel());

        assertNull(shortResponse.getGlobalStatsErrorChannel());
        shortResponse.setGlobalStatsErrorChannel("globalStatsErrorChannel");
        assertEquals("globalStatsErrorChannel", shortResponse.getGlobalStatsErrorChannel());

        assertNull(shortResponse.getGlobalStatsOperatingSystemChangesListenChannel());
        shortResponse.setGlobalStatsOperatingSystemChangesListenChannel("globalStatsOperatingSystemChangesListenChannel");
        assertEquals("globalStatsOperatingSystemChangesListenChannel", shortResponse.getGlobalStatsOperatingSystemChangesListenChannel());

        assertNull(shortResponse.getGlobalStatsBrowserChangesListenChannel());
        shortResponse.setGlobalStatsBrowserChangesListenChannel("globalStatsBrowserChangesListenChannel");
        assertEquals("globalStatsBrowserChangesListenChannel", shortResponse.getGlobalStatsBrowserChangesListenChannel());

        assertNull(shortResponse.getAdsUrl());
        shortResponse.setAdsUrl("adsUrl");
        assertEquals("adsUrl", shortResponse.getAdsUrl());

    }

    @Test
    public void verifyHash(){
        ShortResponse shortResponse = new ShortResponse("0", false, "frontEndRedirectURI", "backEndURI", "backEndWsURI");
        assertEquals(-33606857, shortResponse.hashCode());
    }

    @Test
    public void verifyEquals(){
        ShortResponse shortResponse = new ShortResponse("0", false, "frontEndRedirectURI", "backEndURI", "backEndWsURI");
        ShortResponse shortResponse1 = new ShortResponse("0", false, "frontEndRedirectURI", "backEndURI", "backEndWsURI");

        assertEquals(shortResponse, shortResponse1);
        assertNotEquals(shortResponse, new Object());
        assertNotEquals(shortResponse, null);
    }
}