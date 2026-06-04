package com.recipe.services;

import com.recipe.dao.UserDAO;
import com.recipe.models.User;
import com.recipe.auth.SessionManager;
import com.recipe.auth.PasswordValidator;
import com.recipe.exceptions.AuthException;
import com.recipe.exceptions.DatabaseException;
import com.recipe.exceptions.ValidationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthService {
    
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public User register(String username, String email, String password, String confirmPassword, String fullName) 
            throws ValidationException, DatabaseException, AuthException {
        
        // Validate inputs
        validateRegistrationInputs(username, email, password, confirmPassword);
        
        // Check if username or email already exists
        if (userDAO.usernameExists(username)) {
            throw new ValidationException("Username already taken. Please choose another.");
        }
        
        if (userDAO.emailExists(email)) {
            throw new ValidationException("Email already registered. Please login or use another email.");
        }
        
        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        // Create user
        User user = new User(username, email, hashedPassword);
        user.setFullName(fullName);
        user.setCalorieGoal(2000); // Default daily calorie goal
        
        // Save to database
        User savedUser = userDAO.insertUser(user);
        
        return savedUser;
    }
    
    public User login(String usernameOrEmail, String password) 
            throws AuthException, DatabaseException {
        
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new AuthException("Username or email is required");
        }
        
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password is required");
        }
        
        User user = null;
        
        // Check if input is email or username
        if (usernameOrEmail.contains("@")) {
            user = userDAO.getUserByEmail(usernameOrEmail);
        } else {
            user = userDAO.getUserByUsername(usernameOrEmail);
        }
        
        if (user == null) {
            throw new AuthException("Invalid username/email or password");
        }
        
        // Verify password
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new AuthException("Invalid username/email or password");
        }
        
        // Update last login time
        userDAO.updateLastLogin(user.getId());
        
        // Start session
        sessionManager.login(user);
        
        return user;
    }
    
    public void logout() {
        sessionManager.logout();
    }
    
    public boolean changePassword(int userId, String oldPassword, String newPassword, String confirmNewPassword) 
            throws AuthException, ValidationException, DatabaseException {
        
        // Validate new password
        List<String> passwordErrors = PasswordValidator.validate(newPassword);
        if (!passwordErrors.isEmpty()) {
            throw new ValidationException(passwordErrors);
        }
        
        // Check if new passwords match
        if (!newPassword.equals(confirmNewPassword)) {
            throw new ValidationException("New passwords do not match");
        }
        
        // Get current user
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new AuthException("User not found");
        }
        
        // Verify old password
        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            throw new AuthException("Current password is incorrect");
        }
        
        // Hash new password and update
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        boolean updated = userDAO.updatePassword(userId, newHashedPassword);
        
        if (updated) {
            // Update session user if it's the current user
            if (sessionManager.isLoggedIn() && sessionManager.getCurrentUserId() == userId) {
                user.setPasswordHash(newHashedPassword);
            }
        }
        
        return updated;
    }
    
    public boolean updateProfile(int userId, String email, String fullName, Integer calorieGoal) 
            throws ValidationException, DatabaseException {
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        // Check if email is taken by another user
        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null && existingUser.getId() != userId) {
            throw new ValidationException("Email already in use by another account");
        }
        
        boolean updated = userDAO.updateProfile(userId, email, fullName, calorieGoal);
        
        if (updated && sessionManager.isLoggedIn() && sessionManager.getCurrentUserId() == userId) {
            User currentUser = sessionManager.getCurrentUser();
            currentUser.setEmail(email);
            currentUser.setFullName(fullName);
            if (calorieGoal != null) {
                currentUser.setCalorieGoal(calorieGoal);
            }
        }
        
        return updated;
    }
    
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    private void validateRegistrationInputs(String username, String email, String password, String confirmPassword) 
            throws ValidationException {
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (username.length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }
        
        if (username.length() > 30) {
            throw new ValidationException("Username must be less than 30 characters");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        // Validate password
        List<String> passwordErrors = PasswordValidator.validate(password);
        if (!passwordErrors.isEmpty()) {
            throw new ValidationException(passwordErrors);
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match");
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}