package bluebomb.urlshortener.model;

public class ShortRequest {
    private String HeadURL;
    private String interstitialURL;
    private Integer secondsToRedirect;

    public ShortRequest(String HeadURL, String interstitialURL, Integer secondsToRedirect) {
        this.HeadURL = HeadURL;
        this.interstitialURL = interstitialURL;
        this.secondsToRedirect = secondsToRedirect;
    }

    public String getHeadURL() {
        return HeadURL;
    }

    public void setHeadURL(String HeadURL) {
        this.HeadURL = HeadURL;
    }

    public String getinterstitialURL() {
        return interstitialURL;
    }

    public void setinterstitialURL(String interstitialURL) {
        this.interstitialURL = interstitialURL;
    }

    public Integer getsecondsToRedirect() {
        return secondsToRedirect;
    }

    public void setsecondsToRedirect(Integer secondsToRedirect) {
        this.secondsToRedirect = secondsToRedirect;
    }


}

