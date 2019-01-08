package bluebomb.urlshortener.model;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "RedirectURL{" +
                "secondsToRedirect=" + secondsToRedirect +
                ", interstitialURL='" + interstitialURL + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RedirectURL)) {
            return false;
        }
        RedirectURL redirectURL = (RedirectURL) o;
        return Objects.equals(secondsToRedirect, redirectURL.secondsToRedirect) && Objects.equals(interstitialURL, redirectURL.interstitialURL);
    }
}

