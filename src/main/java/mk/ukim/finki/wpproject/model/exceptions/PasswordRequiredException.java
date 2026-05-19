package mk.ukim.finki.wpproject.model.exceptions;

public class PasswordRequiredException extends RuntimeException {
    public PasswordRequiredException() {
        super("Password fields are required.");
    }
}
