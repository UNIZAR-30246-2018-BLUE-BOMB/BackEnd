package bluebomb.urlshortener.model;

public class ShortenedInfo {
    private String sequence;
    private String headURL;
    private String interstitialURL;
    private Integer secondsToRedirect;

    @SuppressWarnings("unused")
    public ShortenedInfo() {
    }

    @SuppressWarnings("unused")
    public ShortenedInfo(String sequence, String headURL, String interstitialURL, Integer secondsToRedirect) {
        this.sequence = sequence;
        this.headURL = headURL;
        this.interstitialURL = interstitialURL;
        this.secondsToRedirect = secondsToRedirect;
    }

    @SuppressWarnings("unused")
    public String getHeadURL() {
        return headURL;
    }

    @SuppressWarnings("unused")
    public void setHeadURL(String headURL) {
        this.headURL = headURL;
    }

    @SuppressWarnings("unused")
    public void setInterstitialURL(String interstitialURL) {
        this.interstitialURL = interstitialURL;
    }

    @SuppressWarnings("unused")
    public String getInterstitialURL() {
        return interstitialURL;
    }

    @SuppressWarnings("unused")
    public String getSequence() {
        return sequence;
    }

    @SuppressWarnings("unused")
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @SuppressWarnings("unused")
    public Integer getSecondsToRedirect() {
        return secondsToRedirect;
    }

    @SuppressWarnings("unused")
    public void setSecondsToRedirect(Integer secondsToRedirect) {
        this.secondsToRedirect = secondsToRedirect;
    }

}
