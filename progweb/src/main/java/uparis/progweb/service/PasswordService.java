package uparis.progweb.service;

import org.springframework.stereotype.Service;
import uparis.progweb.model.PasswordEntry;
import uparis.progweb.model.PasswordStrength;
import uparis.progweb.util.SecurityUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.security.SecureRandom;

@Service
public class PasswordService {

    // In-memory storage for passwords
    private final Map<String, PasswordEntry> passwordStore = new ConcurrentHashMap<>();

    // Add sample date for demonstration
    public PasswordService() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Add a few sample passwords
        PasswordEntry entry1 = new PasswordEntry(
                "Google Account",
                "user@gmail.com",
                "P@ssw0rd123!");
        entry1.setWebsite("https://google.com");
        entry1.setStrength(PasswordStrength.STRONG);
        entry1.setCreatedAt(LocalDate.now().minusDays(30));
        entry1.setExpiresAt(LocalDate.now().plusDays(60));

        PasswordEntry entry2 = new PasswordEntry(
                "Facebook",
                "user.name",
                "simple123");
        entry2.setWebsite("https://facebook.com");
        entry2.setStrength(PasswordStrength.WEAK);
        entry2.setCreatedAt(LocalDate.now().minusDays(60));
        entry2.setExpiresAt(LocalDate.now().plusDays(30));

        PasswordEntry entry3 = new PasswordEntry(
                "Banking App",
                "user.banking",
                "VeryS3cur3P@ssw0rd!2023");
        entry3.setWebsite("https://mybank.com");
        entry3.setStrength(PasswordStrength.VERY_STRONG);
        entry3.setCreatedAt(LocalDate.now().minusDays(10));
        entry3.setExpiresAt(LocalDate.now().plusDays(80));

        // Expired password example
        PasswordEntry entry4 = new PasswordEntry(
                "Old Email",
                "legacy@email.com",
                "OldP@ss123");
        entry4.setStrength(PasswordStrength.MEDIUM);
        entry4.setCreatedAt(LocalDate.now().minusDays(100));
        entry4.setExpiresAt(LocalDate.now().minusDays(10)); // Expired

        // Add to store
        savePassword(entry1);
        savePassword(entry2);
        savePassword(entry3);
        savePassword(entry4);
    }

    public List<PasswordEntry> getAllPasswords() {
        return new ArrayList<>(passwordStore.values()).stream()
                .peek(PasswordEntry::updateExpirationStatus) // Update expiration status
                .collect(Collectors.toList());
    }

    public Optional<PasswordEntry> getPasswordById(String id) {
        PasswordEntry entry = passwordStore.get(id);
        if (entry != null) {
            entry.updateExpirationStatus();
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    public PasswordEntry savePassword(PasswordEntry passwordEntry) {
        // Ensure ID is set
        if (passwordEntry.getId() == null) {
            passwordEntry.setId(UUID.randomUUID().toString());
        }

        // Set creation date if not provided
        if (passwordEntry.getCreatedAt() == null) {
            passwordEntry.setCreatedAt(LocalDate.now());
        }

        // Set expiration date if not provided (90 days from now)
        if (passwordEntry.getExpiresAt() == null) {
            passwordEntry.setExpiresAt(LocalDate.now().plusDays(90));
        } else {
            passwordEntry.updateExpirationStatus();
        }

        // Calculate password strength if not already set
        if (passwordEntry.getStrength() == null) {
            passwordEntry.setStrength(checkPasswordStrength(passwordEntry.getPassword()));
        }

        passwordStore.put(passwordEntry.getId(), passwordEntry);
        return passwordEntry;
    }

    public boolean deletePassword(String id) {
        return passwordStore.remove(id) != null;
    }

    public PasswordStrength checkPasswordStrength(String password) {
        int score = 0;

        // Length check
        if (password.length() < 8) {
            return PasswordStrength.WEAK;
        } else {
            score += Math.min(password.length() / 4, 3); // Up to 3 points for length
        }

        // Check for different character types
        if (Pattern.compile("[a-z]").matcher(password).find())
            score++; // Has lowercase
        if (Pattern.compile("[A-Z]").matcher(password).find())
            score++; // Has uppercase
        if (Pattern.compile("[0-9]").matcher(password).find())
            score++; // Has digit
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(password).find())
            score++; // Has special char

        // Check for common password patterns/words
        if (SecurityUtil.containsCommonPassword(password)) {
            score -= 3; // Penalty for common passwords
        }

        // Final score ranges
        if (score <= 2)
            return PasswordStrength.WEAK;
        if (score <= 4)
            return PasswordStrength.MEDIUM;
        if (score <= 6)
            return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }

    public String generatePassword(int length, boolean useSpecial) {
        length = Math.max(8, Math.min(length, 32)); // Clamp between 8-32

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder validChars = new StringBuilder();
        validChars.append(lowercase);
        validChars.append(uppercase);
        validChars.append(digits);

        if (useSpecial) {
            validChars.append(special);
        }

        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one of each required character type
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));

        if (useSpecial) {
            password.append(special.charAt(random.nextInt(special.length())));
        }

        // Fill the rest of the password
        for (int i = password.length(); i < length; i++) {
            password.append(validChars.charAt(random.nextInt(validChars.length())));
        }

        // Shuffle the password
        List<Character> chars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);

        StringBuilder shuffled = new StringBuilder(length);
        for (char c : chars) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }
}