package com.recipe.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MealPlan record representing a planned meal for a specific day and meal type.
 */
public record MealPlan(
    int id,
    int userId,
    int recipeId,
    int dayOfWeek,     // 0 = Monday, 1 = Tuesday, ..., 6 = Sunday
    String mealType,   // Breakfast, Lunch, Dinner, Snack
    LocalDate weekStart,
    String recipeTitle,
    String recipePhotoPath
) {
    
    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static final String[] MEAL_TYPES = {"Breakfast", "Lunch", "Dinner", "Snack"};
    
    public String getDayName() {
        if (dayOfWeek >= 0 && dayOfWeek < DAYS_OF_WEEK.length) {
            return DAYS_OF_WEEK[dayOfWeek];
        }
        return "Unknown";
    }
    
    public LocalDate getDate() {
        return weekStart.plusDays(dayOfWeek);
    }
    
    public String getFormattedDate() {
        return getDate().format(DateTimeFormatter.ofPattern("EEE, MMM d"));
    }
    
    public static class Builder {
        private int id;
        private int userId;
        private int recipeId;
        private int dayOfWeek;
        private String mealType;
        private LocalDate weekStart;
        private String recipeTitle;
        private String recipePhotoPath;
        
        public Builder id(int id) { this.id = id; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder recipeId(int recipeId) { this.recipeId = recipeId; return this; }
        public Builder dayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public Builder mealType(String mealType) { this.mealType = mealType; return this; }
        public Builder weekStart(LocalDate weekStart) { this.weekStart = weekStart; return this; }
        public Builder recipeTitle(String recipeTitle) { this.recipeTitle = recipeTitle; return this; }
        public Builder recipePhotoPath(String recipePhotoPath) { this.recipePhotoPath = recipePhotoPath; return this; }
        
        public MealPlan build() {
            return new MealPlan(id, userId, recipeId, dayOfWeek, mealType, weekStart, recipeTitle, recipePhotoPath);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}