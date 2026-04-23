package mk.ukim.finki.wpproject.model.enums;

public enum TextType {
    REQUEST("Request"),
    PROPOSAL("Proposal"),
    REMINDER("Reminder"),
    QUESTION("Question"),
    COMPLAINT("Complaint"),
    FEEDBACK("Feedback"),
    OTHER("Other");

    private final String displayName;

    TextType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
