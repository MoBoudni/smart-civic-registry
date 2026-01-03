package de.behoerde.smartcivicregistry.person.domain.model;

public enum MaritalStatus {
    SINGLE("Ledig"),
    MARRIED("Verheiratet"),
    DIVORCED("Geschieden"),
    WIDOWED("Verwitwet"),
    REGISTERED_PARTNERSHIP("Eingetragene Lebenspartnerschaft");

    private final String displayName;

    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
