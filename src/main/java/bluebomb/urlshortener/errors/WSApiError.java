package bluebomb.urlshortener.errors;

/**
 * Common error message for WebSockets endpoints
 */
public class WSApiError {
    /**
     * Error message
     */
    private String error;

    @SuppressWarnings("unused")
    public WSApiError() {
    }

    @SuppressWarnings("unused")
    public WSApiError(String error) {
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
