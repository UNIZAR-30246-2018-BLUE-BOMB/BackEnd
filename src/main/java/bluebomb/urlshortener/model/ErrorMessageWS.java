package bluebomb.urlshortener.model;

/**
 * Common error message for WebSockets endpoints
 */
public class ErrorMessageWS {
    /**
     * Error message
     */
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
