package mk.ukim.finki.wpproject.model.enums;

public enum TextFormat {
    EMAIL("Email"),
    SMS("SMS"),
    POETRY("Poetry"),
    FORMAL_LETTER("Formal Letter"),
    SOCIAL_MEDIA("Social Media"),
    CHAT("Chat"),
    OTHER("Other");

    private final String displayName;

    TextFormat(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
