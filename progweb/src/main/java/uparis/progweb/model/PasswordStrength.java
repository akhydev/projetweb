package uparis.progweb.model;

public enum PasswordStrength {
    WEAK("Weak"),
    MEDIUM("Medium"),
    STRONG("Strong"),
    VERY_STRONG("Very Strong");

    private final String displayName;

    PasswordStrength(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}