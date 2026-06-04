package com.recipe.auth;

import java.time.LocalDateTime;

import com.recipe.models.User;

/**
 * Singleton SessionManager stores the currently logged-in user.
 * All modules must call SessionManager.getCurrentUser() to get user info.
 * 
 * @author Ahmad (User & Authentication Module Lead)
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    private LocalDateTime loginTime;
    
    private SessionManager() {}
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        System.out.println("User logged in: " + user.getUsername() + " at " + loginTime);
    }
    
    public void logout() {
        this.currentUser = null;
        this.loginTime = null;
        System.out.println("User logged out");
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public int getCurrentUserId() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return currentUser.getId();
    }
}