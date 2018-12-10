package bluebomb.urlshortener.exceptions;

public class ShortenedInfoException extends Exception{
    private String sequence;
    private String username;

    public ShortenedInfoException(String message, String sequence, String username) {
        super(message);
        this.sequence = sequence;
        this.username = username;
    }

    public String getSequence() {
        return sequence;
    }

    public String getUsername() {
        return username;
    }
}
