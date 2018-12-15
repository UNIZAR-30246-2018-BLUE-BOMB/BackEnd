package bluebomb.urlshortener.model;

public class RedirectURL {
    private Integer secondsToRedirect;
    private String interstitialURL;

    public RedirectURL(Integer secondsToRedirect, String interstitialURL) {
        this.secondsToRedirect = secondsToRedirect;
        this.interstitialURL = interstitialURL;
    }

    public Integer getSecondsToRedirect() {
        return secondsToRedirect;
    }

    public void setSecondsToRedirect(Integer secondsToRedirect) {
        this.secondsToRedirect = secondsToRedirect;
    }

    public String getInterstitialURL() {
        return interstitialURL;
    }

    public void setInterstitialURL(String interstitialURL) {
        this.interstitialURL = interstitialURL;
    }
}

