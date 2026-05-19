package mk.ukim.finki.wpproject.model.exceptions;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() {
        super("The request contains invalid or missing data.");
    }
}
