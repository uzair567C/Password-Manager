package com.recipe.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe model using Java Record for immutable data.
 * For mutable operations, use Recipe.Builder or RecipeDTO.
 */
public record Recipe(
    int id,
    int userId,
    String title,
    String description,
    int prepTime,
    int cookTime,
    int servings,
    String difficulty,
    Integer categoryId,
    String photoPath,
    double cost,
    int cookCount,
    double averageRating,
    boolean isDraft,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<RecipeIngredient> ingredients,
    Nutrition nutrition
) {
    
    public int getTotalTime() {
        return prepTime + cookTime;
    }
    
    public String getFormattedTotalTime() {
        int total = getTotalTime();
        if (total < 60) return total + " mins";
        int hours = total / 60;
        int mins = total % 60;
        return mins == 0 ? hours + " hr" : hours + " hr " + mins + " min";
    }
    
    public double getCostPerServing() {
        return servings > 0 ? cost / servings : cost;
    }
    
    // Builder pattern for mutable construction
    public static class Builder {
        private int id;
        private int userId;
        private String title;
        private String description;
        private int prepTime;
        private int cookTime;
        private int servings = 4;
        private String difficulty = "Medium";
        private Integer categoryId;
        private String photoPath;
        private double cost;
        private int cookCount;
        private double averageRating;
        private boolean isDraft;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<RecipeIngredient> ingredients = new ArrayList<>();
        private Nutrition nutrition;
        
        public Builder id(int id) { this.id = id; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder prepTime(int prepTime) { this.prepTime = prepTime; return this; }
        public Builder cookTime(int cookTime) { this.cookTime = cookTime; return this; }
        public Builder servings(int servings) { this.servings = servings; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder categoryId(Integer categoryId) { this.categoryId = categoryId; return this; }
        public Builder photoPath(String photoPath) { this.photoPath = photoPath; return this; }
        public Builder cost(double cost) { this.cost = cost; return this; }
        public Builder cookCount(int cookCount) { this.cookCount = cookCount; return this; }
        public Builder averageRating(double averageRating) { this.averageRating = averageRating; return this; }
        public Builder isDraft(boolean isDraft) { this.isDraft = isDraft; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder ingredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; return this; }
        public Builder nutrition(Nutrition nutrition) { this.nutrition = nutrition; return this; }
        
        public Recipe build() {
            return new Recipe(id, userId, title, description, prepTime, cookTime, 
                servings, difficulty, categoryId, photoPath, cost, cookCount, 
                averageRating, isDraft, createdAt, updatedAt, ingredients, nutrition);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Copy constructor style
    public Recipe withUpdatedFields(Builder builder) {
        return new Recipe(
            id, userId, title, description, prepTime, cookTime, servings, difficulty,
            categoryId, photoPath, cost, cookCount, averageRating, isDraft,
            createdAt, LocalDateTime.now(), ingredients, nutrition
        );
    }
}