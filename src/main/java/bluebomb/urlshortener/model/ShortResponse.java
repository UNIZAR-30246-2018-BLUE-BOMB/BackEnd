package bluebomb.urlshortener.model;


import java.util.Objects;

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

    public ShortResponse(String sequence, boolean hadAds, final String frontEndRedirectURI, final String backEndURI, final String backEndWsURI) {
        this.sequence = sequence;
        this.shortedUrl = frontEndRedirectURI + "/" + sequence;
        this.qrReferenceUrl = backEndURI + "/" + sequence + "/qr";

        this.infoUrlRequestChannel = backEndWsURI + "/app/info";
        this.infoUrlListenChannel = backEndWsURI + "/user/info/" + sequence;
        this.infoUrlErrorChannel = backEndWsURI + "/user/queue/error/info";

        this.dailyStatsOperatingSystemUrl = backEndURI + "/" + sequence + "/stats/os/daily";
        this.dailyStatsBrowserUrl = backEndURI + "/" + sequence + "/stats/browser/daily";

        this.globalStatsRequestOperatingSystemChannel = backEndWsURI + "/app/stats/global/os";
        this.globalStatsRequestBrowserChannel = backEndWsURI + "/app/stats/global/browser";

        this.globalStatsListenChannel = backEndWsURI + "/user/stats/global";
        this.globalStatsErrorChannel = backEndWsURI + "/user/queue/error/stats/global";

        this.globalStatsOperatingSystemChangesListenChannel = backEndWsURI + "/topic/stats/global/os/" + sequence;
        this.globalStatsBrowserChangesListenChannel = backEndWsURI + "/topic/stats/global/browser/" + sequence;

        this.adsUrl = hadAds ? backEndURI + "/" + sequence + "/ads" : "";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortResponse that = (ShortResponse) o;
        return Objects.equals(sequence, that.sequence) &&
                Objects.equals(shortedUrl, that.shortedUrl) &&
                Objects.equals(qrReferenceUrl, that.qrReferenceUrl) &&
                Objects.equals(infoUrlRequestChannel, that.infoUrlRequestChannel) &&
                Objects.equals(infoUrlListenChannel, that.infoUrlListenChannel) &&
                Objects.equals(infoUrlErrorChannel, that.infoUrlErrorChannel) &&
                Objects.equals(dailyStatsOperatingSystemUrl, that.dailyStatsOperatingSystemUrl) &&
                Objects.equals(dailyStatsBrowserUrl, that.dailyStatsBrowserUrl) &&
                Objects.equals(globalStatsRequestOperatingSystemChannel, that.globalStatsRequestOperatingSystemChannel) &&
                Objects.equals(globalStatsRequestBrowserChannel, that.globalStatsRequestBrowserChannel) &&
                Objects.equals(globalStatsListenChannel, that.globalStatsListenChannel) &&
                Objects.equals(globalStatsErrorChannel, that.globalStatsErrorChannel) &&
                Objects.equals(globalStatsOperatingSystemChangesListenChannel, that.globalStatsOperatingSystemChangesListenChannel) &&
                Objects.equals(globalStatsBrowserChangesListenChannel, that.globalStatsBrowserChangesListenChannel) &&
                Objects.equals(adsUrl, that.adsUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, shortedUrl, qrReferenceUrl, infoUrlRequestChannel, infoUrlListenChannel, infoUrlErrorChannel, dailyStatsOperatingSystemUrl, dailyStatsBrowserUrl, globalStatsRequestOperatingSystemChannel, globalStatsRequestBrowserChannel, globalStatsListenChannel, globalStatsErrorChannel, globalStatsOperatingSystemChangesListenChannel, globalStatsBrowserChangesListenChannel, adsUrl);
    }
}