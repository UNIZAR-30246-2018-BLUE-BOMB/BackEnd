package bluebomb.urlshortener.model;

import bluebomb.urlshortener.config.CommonValues;

public class ShortResponse {
    private String sequence;
    private String shortedUrl;
    private String qrReferenceUrl;

    private String infoUrlRequestChannel;
    private String infoUrlListenChannel;
    private String infoUrlErrorChannel;

    private String dailyStatsOperatingSystemUrl;
    private String dailyStatsBrowserUrl;

    private String globalStatsRequestOperatingSystemChannel;
    private String globalStatsRequestBrowserChannel;
    private String globalStatsListenChannel;
    private String globalStatsErrorChannel;

    private String globalStatsOperatingSystemChangesListenChannel;
    private String globalStatsBrowserChangesListenChannel;

    private String adsUrl;

    public ShortResponse() {
    }

    public ShortResponse(String sequence, boolean hadAds) {
        this.sequence = sequence;
        this.shortedUrl = CommonValues.FRONT_END_REDIRECT_URI + "/"  + sequence;
        this.qrReferenceUrl = CommonValues.BACK_END_URI + "/" + sequence + "/qr";

        this.infoUrlRequestChannel = CommonValues.BACK_END_WS_URI + "/app/info";
        this.infoUrlListenChannel = CommonValues.BACK_END_WS_URI + "/user/info/" + sequence;
        this.infoUrlErrorChannel = CommonValues.BACK_END_WS_URI + "/user/queue/error/info";

        this.dailyStatsOperatingSystemUrl = CommonValues.BACK_END_URI + "/" + sequence + "/stats/os/daily";
        this.dailyStatsBrowserUrl = CommonValues.BACK_END_URI + "/" + sequence + "/stats/browser/daily";

        this.globalStatsRequestOperatingSystemChannel = CommonValues.BACK_END_WS_URI + "/app/stats/global/os";
        this.globalStatsRequestBrowserChannel = CommonValues.BACK_END_WS_URI + "/app/stats/global/browser";

        this.globalStatsListenChannel = CommonValues.BACK_END_WS_URI + "/user/stats/global";
        this.globalStatsErrorChannel = CommonValues.BACK_END_WS_URI + "/user/queue/error/stats/global";

        this.globalStatsOperatingSystemChangesListenChannel = CommonValues.BACK_END_WS_URI + "/topic/stats/global/os/" + sequence;
        this.globalStatsBrowserChangesListenChannel = CommonValues.BACK_END_WS_URI + "/topic/stats/global/browser/" + sequence;

        this.adsUrl = hadAds ? CommonValues.BACK_END_URI + "/" + sequence + "/ads" : "";
    }

    public ShortResponse(String sequence, String shortedUrl, String qrReferenceUrl, String infoUrlRequestChannel,
                         String infoUrlListenChannel, String infoUrlErrorChannel, String dailyStatsOperatingSystemUrl,
                         String dailyStatsBrowserUrl, String globalStatsRequestOperatingSystemChannel,
                         String globalStatsRequestBrowserChannel, String globalStatsListenChannel,
                         String globalStatsErrorChannel, String globalStatsOperatingSystemChangesListenChannel,
                         String globalStatsBrowserChangesListenChannel, String adsUrl) {
        this.sequence = sequence;
        this.shortedUrl = shortedUrl;
        this.qrReferenceUrl = qrReferenceUrl;
        this.infoUrlRequestChannel = infoUrlRequestChannel;
        this.infoUrlListenChannel = infoUrlListenChannel;
        this.infoUrlErrorChannel = infoUrlErrorChannel;
        this.dailyStatsOperatingSystemUrl = dailyStatsOperatingSystemUrl;
        this.dailyStatsBrowserUrl = dailyStatsBrowserUrl;
        this.globalStatsRequestOperatingSystemChannel = globalStatsRequestOperatingSystemChannel;
        this.globalStatsRequestBrowserChannel = globalStatsRequestBrowserChannel;
        this.globalStatsListenChannel = globalStatsListenChannel;
        this.globalStatsErrorChannel = globalStatsErrorChannel;
        this.globalStatsOperatingSystemChangesListenChannel = globalStatsOperatingSystemChangesListenChannel;
        this.globalStatsBrowserChangesListenChannel = globalStatsBrowserChangesListenChannel;
        this.adsUrl = adsUrl;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getShortedUrl() {
        return shortedUrl;
    }

    public void setShortedUrl(String shortedUrl) {
        this.shortedUrl = shortedUrl;
    }

    public String getQrReferenceUrl() {
        return qrReferenceUrl;
    }

    public void setQrReferenceUrl(String qrReferenceUrl) {
        this.qrReferenceUrl = qrReferenceUrl;
    }

    public String getInfoUrlRequestChannel() {
        return infoUrlRequestChannel;
    }

    public void setInfoUrlRequestChannel(String infoUrlRequestChannel) {
        this.infoUrlRequestChannel = infoUrlRequestChannel;
    }

    public String getInfoUrlListenChannel() {
        return infoUrlListenChannel;
    }

    public void setInfoUrlListenChannel(String infoUrlListenChannel) {
        this.infoUrlListenChannel = infoUrlListenChannel;
    }

    public String getInfoUrlErrorChannel() {
        return infoUrlErrorChannel;
    }

    public void setInfoUrlErrorChannel(String infoUrlErrorChannel) {
        this.infoUrlErrorChannel = infoUrlErrorChannel;
    }

    public String getDailyStatsOperatingSystemUrl() {
        return dailyStatsOperatingSystemUrl;
    }

    public void setDailyStatsOperatingSystemUrl(String dailyStatsOperatingSystemUrl) {
        this.dailyStatsOperatingSystemUrl = dailyStatsOperatingSystemUrl;
    }

    public String getDailyStatsBrowserUrl() {
        return dailyStatsBrowserUrl;
    }

    public void setDailyStatsBrowserUrl(String dailyStatsBrowserUrl) {
        this.dailyStatsBrowserUrl = dailyStatsBrowserUrl;
    }

    public String getGlobalStatsRequestOperatingSystemChannel() {
        return globalStatsRequestOperatingSystemChannel;
    }

    public void setGlobalStatsRequestOperatingSystemChannel(String globalStatsRequestOperatingSystemChannel) {
        this.globalStatsRequestOperatingSystemChannel = globalStatsRequestOperatingSystemChannel;
    }

    public String getGlobalStatsRequestBrowserChannel() {
        return globalStatsRequestBrowserChannel;
    }

    public void setGlobalStatsRequestBrowserChannel(String globalStatsRequestBrowserChannel) {
        this.globalStatsRequestBrowserChannel = globalStatsRequestBrowserChannel;
    }

    public String getGlobalStatsListenChannel() {
        return globalStatsListenChannel;
    }

    public void setGlobalStatsListenChannel(String globalStatsListenChannel) {
        this.globalStatsListenChannel = globalStatsListenChannel;
    }

    public String getGlobalStatsErrorChannel() {
        return globalStatsErrorChannel;
    }

    public void setGlobalStatsErrorChannel(String globalStatsErrorChannel) {
        this.globalStatsErrorChannel = globalStatsErrorChannel;
    }

    public String getGlobalStatsOperatingSystemChangesListenChannel() {
        return globalStatsOperatingSystemChangesListenChannel;
    }

    public void setGlobalStatsOperatingSystemChangesListenChannel(String globalStatsOperatingSystemChangesListenChannel) {
        this.globalStatsOperatingSystemChangesListenChannel = globalStatsOperatingSystemChangesListenChannel;
    }

    public String getGlobalStatsBrowserChangesListenChannel() {
        return globalStatsBrowserChangesListenChannel;
    }

    public void setGlobalStatsBrowserChangesListenChannel(String globalStatsBrowserChangesListenChannel) {
        this.globalStatsBrowserChangesListenChannel = globalStatsBrowserChangesListenChannel;
    }

    public String getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }
}