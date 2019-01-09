package bluebomb.urlshortener.model;

import java.util.Objects;

public class ShortenedInfo {
    private String sequence;
    private String headURL;
    private String interstitialURL;
    private Integer secondsToRedirect;


    public ShortenedInfo() {
    }


    public ShortenedInfo(String sequence, String headURL, String interstitialURL, Integer secondsToRedirect) {
        this.sequence = sequence;
        this.headURL = headURL;
        this.interstitialURL = interstitialURL;
        this.secondsToRedirect = secondsToRedirect;
    }


    public String getHeadURL() {
        return headURL;
    }


    public void setHeadURL(String headURL) {
        this.headURL = headURL;
    }


    public void setInterstitialURL(String interstitialURL) {
        this.interstitialURL = interstitialURL;
    }


    public String getInterstitialURL() {
        return interstitialURL;
    }


    public String getSequence() {
        return sequence;
    }


    public void setSequence(String sequence) {
        this.sequence = sequence;
    }


    public Integer getSecondsToRedirect() {
        return secondsToRedirect;
    }


    public void setSecondsToRedirect(Integer secondsToRedirect) {
        this.secondsToRedirect = secondsToRedirect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortenedInfo that = (ShortenedInfo) o;
        return Objects.equals(sequence, that.sequence) &&
                Objects.equals(headURL, that.headURL) &&
                Objects.equals(interstitialURL, that.interstitialURL) &&
                Objects.equals(secondsToRedirect, that.secondsToRedirect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, headURL, interstitialURL, secondsToRedirect);
    }
}
