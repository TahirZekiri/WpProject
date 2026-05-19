package mk.ukim.finki.wpproject.model.exceptions;

public class PasswordConfirmationMismatchException extends RuntimeException {
    public PasswordConfirmationMismatchException() {
        super("Password and confirmation password do not match.");
    }
}
