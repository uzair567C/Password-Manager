package com.recipe.models;

import java.time.LocalDateTime;

/**
 * Rating record for user reviews on recipes.
 */
public record Rating(
    int id,
    int userId,
    int recipeId,
    int stars,
    String reviewText,
    LocalDateTime createdAt,
    String username
) {
    
    public static final int MAX_STARS = 5;
    public static final int MIN_STARS = 1;
    
    public boolean isValidStars() {
        return stars >= MIN_STARS && stars <= MAX_STARS;
    }
    
    public String getStarDisplay() {
        return "★".repeat(stars) + "☆".repeat(MAX_STARS - stars);
    }
    
    public static class Builder {
        private int id;
        private int userId;
        private int recipeId;
        private int stars;
        private String reviewText;
        private LocalDateTime createdAt;
        private String username;
        
        public Builder id(int id) { this.id = id; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder recipeId(int recipeId) { this.recipeId = recipeId; return this; }
        public Builder stars(int stars) { this.stars = stars; return this; }
        public Builder reviewText(String reviewText) { this.reviewText = reviewText; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder username(String username) { this.username = username; return this; }
        
        public Rating build() {
            return new Rating(id, userId, recipeId, stars, reviewText, createdAt, username);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}