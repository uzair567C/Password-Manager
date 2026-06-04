package com.recipe.models;

import java.util.List;
/**
 * Search filter parameters for recipe discovery.
 */
public record SearchFilter(
    String searchTerm,
    Integer minPrepTime,
    Integer maxPrepTime,
    Integer minCookTime,
    Integer maxCookTime,
    Integer minCalories,
    Integer maxCalories,
    Integer minServings,
    Integer maxServings,
    Double minRating,
    String difficulty,
    String category,
    String dietType,
    List<String> ingredients,
    Boolean isFavourite,
    Double maxCost
) {
    
    public static class Builder {
        private String searchTerm;
        private Integer minPrepTime;
        private Integer maxPrepTime;
        private Integer minCookTime;
        private Integer maxCookTime;
        private Integer minCalories;
        private Integer maxCalories;
        private Integer minServings;
        private Integer maxServings;
        private Double minRating;
        private String difficulty;
        private String category;
        private String dietType;
        private List<String> ingredients;
        private Boolean isFavourite;
        private Double maxCost;
        
        public Builder searchTerm(String searchTerm) { this.searchTerm = searchTerm; return this; }
        public Builder minPrepTime(Integer minPrepTime) { this.minPrepTime = minPrepTime; return this; }
        public Builder maxPrepTime(Integer maxPrepTime) { this.maxPrepTime = maxPrepTime; return this; }
        public Builder minCookTime(Integer minCookTime) { this.minCookTime = minCookTime; return this; }
        public Builder maxCookTime(Integer maxCookTime) { this.maxCookTime = maxCookTime; return this; }
        public Builder minCalories(Integer minCalories) { this.minCalories = minCalories; return this; }
        public Builder maxCalories(Integer maxCalories) { this.maxCalories = maxCalories; return this; }
        public Builder minServings(Integer minServings) { this.minServings = minServings; return this; }
        public Builder maxServings(Integer maxServings) { this.maxServings = maxServings; return this; }
        public Builder minRating(Double minRating) { this.minRating = minRating; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder dietType(String dietType) { this.dietType = dietType; return this; }
        public Builder ingredients(List<String> ingredients) { this.ingredients = ingredients; return this; }
        public Builder isFavourite(Boolean isFavourite) { this.isFavourite = isFavourite; return this; }
        public Builder maxCost(Double maxCost) { this.maxCost = maxCost; return this; }
        
        public SearchFilter build() {
            return new SearchFilter(searchTerm, minPrepTime, maxPrepTime, minCookTime, maxCookTime,
                minCalories, maxCalories, minServings, maxServings, minRating, difficulty,
                category, dietType, ingredients, isFavourite, maxCost);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public boolean isEmpty() {
        return searchTerm == null && minPrepTime == null && maxPrepTime == null &&
               minCookTime == null && maxCookTime == null && minCalories == null &&
               maxCalories == null && minServings == null && maxServings == null &&
               minRating == null && difficulty == null && category == null &&
               dietType == null && (ingredients == null || ingredients.isEmpty()) &&
               isFavourite == null && maxCost == null;
    }
}