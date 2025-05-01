package uparis.progweb.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SecurityUtil {

    // Common weak passwords
    private static final Set<String> COMMON_PASSWORDS = new HashSet<>(Arrays.asList(
            "password", "123456", "qwerty", "admin", "welcome", "login",
            "abc123", "password123", "admin123", "letmein", "monkey", "1234567890",
            "12345", "123456789", "iloveyou", "1234567", "1234"));

    /**
     * Check if a password contains any common weak passwords
     */
    public static boolean containsCommonPassword(String password) {
        String lowerPass = password.toLowerCase();
        return COMMON_PASSWORDS.stream()
                .anyMatch(lowerPass::contains);
    }

    /**
     * Simple method to obscure a password for display
     */
    public static String maskPassword(String password) {
        if (password == null || password.length() < 2) {
            return "******";
        }

        int visibleChars = Math.min(2, password.length() / 4);
        StringBuilder masked = new StringBuilder();

        // Show first n chars
        masked.append(password, 0, visibleChars);

        // Add asterisks
        for (int i = 0; i < password.length() - (2 * visibleChars); i++) {
            masked.append('*');
        }

        // Show last n chars
        masked.append(password.substring(password.length() - visibleChars));

        return masked.toString();
    }
}