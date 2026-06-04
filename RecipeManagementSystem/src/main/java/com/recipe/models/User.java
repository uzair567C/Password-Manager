package com.recipe.models;

import java.time.LocalDateTime;

/**
 * User model class - maps to 'users' database table.
 * 
 * @author Ahmad (User & Authentication Module Lead)
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private String fullName;
    private int totalRecipes;
    private int totalCookCount;
    private int calorieGoal; // Daily calorie goal for nutrition tracking
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.calorieGoal = 2000; // Default daily goal
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public int getTotalRecipes() { return totalRecipes; }
    public void setTotalRecipes(int totalRecipes) { this.totalRecipes = totalRecipes; }
    
    public int getTotalCookCount() { return totalCookCount; }
    public void setTotalCookCount(int totalCookCount) { this.totalCookCount = totalCookCount; }
    
    public int getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(int calorieGoal) { this.calorieGoal = calorieGoal; }
    
    @Override
    public String toString() {
        return username;
    }
}