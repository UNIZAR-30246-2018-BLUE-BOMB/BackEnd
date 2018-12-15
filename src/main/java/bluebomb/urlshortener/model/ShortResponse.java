package bluebomb.urlshortener.model;

public class ShortResponse {
    private String shortedURL;
    private String qrReference;
    private String dailyStatsReference;
    private String globalStatsReference;
    private String infoReference;

    public ShortResponse(String shortedURL, String qrReference, String dailyStatsReference, String globalStatsReference,
                         String infoReference) {
        this.shortedURL = shortedURL;
        this.qrReference = qrReference;
        this.dailyStatsReference = dailyStatsReference;
        this.globalStatsReference = globalStatsReference;
        this.infoReference = infoReference;
    }

    public String getshortedURL() {
        return shortedURL;
    }

    public void setshortedURL(String shortedURL) {
        this.shortedURL = shortedURL;
    }

    public String getqrReference() {
        return qrReference;
    }

    public void setqrReference(String qrReference) {
        this.qrReference = qrReference;
    }

    public String getdailyStatsReference() {
        return dailyStatsReference;
    }

    public void setdailyStatsReference(String dailyStatsReference) {
        this.dailyStatsReference = dailyStatsReference;
    }

    public String getglobalStatsReference() {
        return globalStatsReference;
    }

    public void setglobalStatsReference(String globalStatsReference) {
        this.globalStatsReference = globalStatsReference;
    }

    public String getinfoReference() {
        return infoReference;
    }

    public void setinfoReference(String infoReference) {
        this.infoReference = infoReference;
    }
}