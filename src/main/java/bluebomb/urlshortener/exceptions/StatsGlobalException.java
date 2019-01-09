package bluebomb.urlshortener.exceptions;

public class StatsGlobalException extends Exception{
    private final String username;

    public StatsGlobalException(String message, String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
