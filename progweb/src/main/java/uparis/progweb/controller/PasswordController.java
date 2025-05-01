package uparis.progweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uparis.progweb.model.PasswordEntry;
import uparis.progweb.model.PasswordStrength;
import uparis.progweb.service.PasswordService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PasswordController {

    private final PasswordService passwordService;

    @Autowired
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<PasswordEntry> passwords = passwordService.getAllPasswords();
        model.addAttribute("passwords", passwords);

        // Count statistics
        long weakCount = passwords.stream()
                .filter(p -> p.getStrength() == PasswordStrength.WEAK)
                .count();

        long strongCount = passwords.stream()
                .filter(p -> p.getStrength() == PasswordStrength.STRONG
                        || p.getStrength() == PasswordStrength.VERY_STRONG)
                .count();

        long expiringCount = passwords.stream()
                .filter(p -> p.isExpiringSoon() || p.isExpired())
                .count();

        model.addAttribute("weakCount", weakCount);
        model.addAttribute("strongCount", strongCount);
        model.addAttribute("expiringCount", expiringCount);

        return "index";
    }

    // API endpoints

    @GetMapping("/api/passwords")
    @ResponseBody
    public List<PasswordEntry> getPasswords() {
        return passwordService.getAllPasswords();
    }

    @PostMapping("/api/passwords")
    @ResponseBody
    public ResponseEntity<PasswordEntry> createPassword(@RequestBody PasswordEntry passwordEntry) {
        // Set expiration date to 90 days from now by default
        if (passwordEntry.getExpiresAt() == null) {
            passwordEntry.setExpiresAt(LocalDate.now().plusDays(90));
        }

        // Calculate strength if not provided
        if (passwordEntry.getStrength() == null) {
            passwordEntry.setStrength(passwordService.checkPasswordStrength(passwordEntry.getPassword()));
        }

        // Set creation date
        passwordEntry.setCreatedAt(LocalDate.now());

        PasswordEntry saved = passwordService.savePassword(passwordEntry);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/api/passwords/{id}")
    @ResponseBody
    public ResponseEntity<PasswordEntry> getPasswordById(@PathVariable String id) {
        Optional<PasswordEntry> passwordOpt = passwordService.getPasswordById(id);
        return passwordOpt
                .map(password -> new ResponseEntity<>(password, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/api/passwords/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletePassword(@PathVariable String id) {
        boolean deleted = passwordService.deletePassword(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/check-strength")
    @ResponseBody
    public Map<String, Object> checkPasswordStrength(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        PasswordStrength strength = passwordService.checkPasswordStrength(password);

        Map<String, Object> response = new HashMap<>();
        response.put("strength", strength.name());
        response.put("display", strength.getDisplayName());

        // Add numeric level for strength meter (0-3)
        int level = 0;
        switch (strength) {
            case WEAK:
                level = 0;
                break;
            case MEDIUM:
                level = 1;
                break;
            case STRONG:
                level = 2;
                break;
            case VERY_STRONG:
                level = 3;
                break;
        }
        response.put("level", level);

        return response;
    }

    @GetMapping("/api/generate-password")
    @ResponseBody
    public Map<String, String> generatePassword(
            @RequestParam(defaultValue = "16") int length,
            @RequestParam(defaultValue = "true") boolean special) {
        String password = passwordService.generatePassword(length, special);
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        return response;
    }

    @GetMapping("/citations-link")
    public String getCitationLink(Model model) {
        // Ajouter l'URL du service de citations au modèle
        // En production, récupérer cette URL dynamiquement
        model.addAttribute("citationsUrl", "http://service-citations:8081/api/citations/random");
        return "citations-link"; // page HTML avec le lien
    }
}