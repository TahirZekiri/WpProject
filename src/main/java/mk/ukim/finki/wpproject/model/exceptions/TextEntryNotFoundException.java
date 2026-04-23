package mk.ukim.finki.wpproject.model.exceptions;

public class TextEntryNotFoundException extends RuntimeException {
    public TextEntryNotFoundException(Long id) {
        super("Text entry with ID " + id + " was not found");
    }
}
