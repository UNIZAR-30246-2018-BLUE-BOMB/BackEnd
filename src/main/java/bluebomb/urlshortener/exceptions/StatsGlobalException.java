package bluebomb.urlshortener.exceptions;

public class StatsGlobalException extends Exception{
    private String username;

    public StatsGlobalException(String message, String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
