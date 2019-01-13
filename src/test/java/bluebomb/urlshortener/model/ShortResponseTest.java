package bluebomb.urlshortener.model;

import org.junit.Test;

import static org.junit.Assert.*;

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

        assertEquals("0", shortResponse.getSequence());
        assertEquals("frontEndRedirectURI/0", shortResponse.getShortedUrl());
        assertEquals("backEndURI/0/qr", shortResponse.getQrReferenceUrl());
        assertEquals("backEndWsURI/app/info", shortResponse.getInfoUrlRequestChannel());
        assertEquals("backEndWsURI/user/info/0", shortResponse.getInfoUrlListenChannel());
        assertEquals("backEndWsURI/user/queue/error/info", shortResponse.getInfoUrlErrorChannel());
        assertEquals("backEndURI/0/stats/os/daily", shortResponse.getDailyStatsOperatingSystemUrl());
        assertEquals("backEndURI/0/stats/browser/daily", shortResponse.getDailyStatsBrowserUrl());
        assertEquals("backEndWsURI/app/stats/global/os", shortResponse.getGlobalStatsRequestOperatingSystemChannel());
        assertEquals("backEndWsURI/app/stats/global/browser", shortResponse.getGlobalStatsRequestBrowserChannel());
        assertEquals("backEndWsURI/user/stats/global", shortResponse.getGlobalStatsListenChannel());
        assertEquals("backEndWsURI/user/queue/error/stats/global", shortResponse.getGlobalStatsErrorChannel());
        assertEquals("backEndWsURI/topic/stats/global/os/0", shortResponse.getGlobalStatsOperatingSystemChangesListenChannel());
        assertEquals("backEndWsURI/topic/stats/global/browser/0", shortResponse.getGlobalStatsBrowserChangesListenChannel());
        assertEquals("", shortResponse.getAdsUrl());

        shortResponse = new ShortResponse("0", true, "frontEndRedirectURI", "backEndURI", "backEndWsURI");
        assertEquals("backEndURI/0/ads", shortResponse.getAdsUrl());


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