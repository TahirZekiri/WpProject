package mk.ukim.finki.wpproject.model.enums;

public enum TextTone {
    FRIENDLY("Friendly"),
    FORMAL("Formal"),
    CASUAL("Casual"),
    ANGRY("Angry"),
    URGENT("Urgent"),
    SARCASTIC("Sarcastic"),
    NEUTRAL("Neutral");

    private final String displayName;

    TextTone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
