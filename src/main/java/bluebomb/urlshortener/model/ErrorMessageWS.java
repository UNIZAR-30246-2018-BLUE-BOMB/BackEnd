package bluebomb.urlshortener.model;

public class ErrorMessageWS {
    private String error;

    @SuppressWarnings("unused")
    public ErrorMessageWS() {
    }

    @SuppressWarnings("unused")
    public ErrorMessageWS(String error) {
        this.error = error;
    }

    @SuppressWarnings("unused")
    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    public void setError(String error) {
        this.error = error;
    }
}
