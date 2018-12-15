package bluebomb.urlshortener.exceptions;

public class ShortenedInfoException extends Exception{
    private String username;

    public ShortenedInfoException(String message, String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
