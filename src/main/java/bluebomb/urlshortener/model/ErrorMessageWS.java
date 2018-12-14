package bluebomb.urlshortener.model;

public class ErrorMessageWS {
    String error;

    public ErrorMessageWS() {
    }

    public ErrorMessageWS(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
