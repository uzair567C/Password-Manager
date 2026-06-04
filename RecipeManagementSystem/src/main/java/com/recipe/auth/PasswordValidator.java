package com.recipe.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;
    
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    
    public static List<String> validate(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.isEmpty()) {
            errors.add("Password cannot be empty");
            return errors;
        }
        
        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_LENGTH) {
            errors.add("Password must be less than " + MAX_LENGTH + " characters");
        }
        
        if (!UPPERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }
        
        if (!LOWERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }
        
        if (!DIGIT.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }
        
        if (!SPECIAL.matcher(password).find()) {
            errors.add("Password must contain at least one special character (!@#$%^&* etc.)");
        }
        
        return errors;
    }
    
    public static int calculateStrength(String password) {
        int strength = 0;
        
        if (password == null || password.isEmpty()) return 0;
        
        if (password.length() >= 8) strength += 20;
        if (password.length() >= 12) strength += 10;
        
        if (UPPERCASE.matcher(password).find()) strength += 20;
        if (LOWERCASE.matcher(password).find()) strength += 20;
        if (DIGIT.matcher(password).find()) strength += 20;
        if (SPECIAL.matcher(password).find()) strength += 20;
        
        // Check for common patterns (penalty)
        String lowerPass = password.toLowerCase();
        if (lowerPass.contains("password") || lowerPass.contains("123456") || 
            lowerPass.contains("qwerty") || lowerPass.contains("admin")) {
            strength = Math.max(0, strength - 30);
        }
        
        return Math.min(100, strength);
    }
    
    public static String getStrengthLabel(int strength) {
        if (strength < 30) return "Very Weak";
        if (strength < 50) return "Weak";
        if (strength < 70) return "Fair";
        if (strength < 85) return "Strong";
        return "Very Strong";
    }
}