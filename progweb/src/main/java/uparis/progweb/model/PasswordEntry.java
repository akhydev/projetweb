package uparis.progweb.model;

import java.time.LocalDate;
import java.util.UUID;

public class PasswordEntry {
    private String id;
    private String name;
    private String username;
    private String password;
    private String website;
    private PasswordStrength strength;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    private boolean expired;
    private boolean expiringSoon;

    // Constructors
    public PasswordEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public PasswordEntry(String name, String username, String password) {
        this();
        this.name = name;
        this.username = username;
        this.password = password;
        this.createdAt = LocalDate.now();
        this.expiresAt = LocalDate.now().plusDays(90);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public void setStrength(PasswordStrength strength) {
        this.strength = strength;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
        updateExpirationStatus();
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isExpiringSoon() {
        return expiringSoon;
    }

    // Helper method to update expiration status
    public void updateExpirationStatus() {
        LocalDate now = LocalDate.now();
        LocalDate oneWeekFromNow = now.plusWeeks(1);

        this.expired = now.isAfter(expiresAt) || now.isEqual(expiresAt);
        this.expiringSoon = !expired && (oneWeekFromNow.isAfter(expiresAt) || oneWeekFromNow.isEqual(expiresAt));
    }
}