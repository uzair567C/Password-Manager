package com.recipe.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Nutrition(
    int recipeId,
    int calories,
    BigDecimal protein,
    BigDecimal carbs,
    BigDecimal fat,
    BigDecimal fiber,
    BigDecimal sugar,
    BigDecimal sodium
) {
    
    public static final Nutrition EMPTY = new Nutrition(0, 0, BigDecimal.ZERO, 
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    
    public double getProteinDouble() { 
        return protein != null ? protein.doubleValue() : 0.0; 
    }
    
    public double getCarbsDouble() { 
        return carbs != null ? carbs.doubleValue() : 0.0; 
    }
    
    public double getFatDouble() { 
        return fat != null ? fat.doubleValue() : 0.0; 
    }
    
    public int getCaloriesPerServing(int servings) {
        return servings > 0 ? calories / servings : calories;
    }
    
    public double getProteinPerServing(int servings) {
        if (servings <= 0 || protein == null) return 0.0;
        return protein.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double getCarbsPerServing(int servings) {
        if (servings <= 0 || carbs == null) return 0.0;
        return carbs.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double getFatPerServing(int servings) {
        if (servings <= 0 || fat == null) return 0.0;
        return fat.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double getFiberPerServing(int servings) {
        if (servings <= 0 || fiber == null) return 0.0;
        return fiber.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double getSugarPerServing(int servings) {
        if (servings <= 0 || sugar == null) return 0.0;
        return sugar.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double getSodiumPerServing(int servings) {
        if (servings <= 0 || sodium == null) return 0.0;
        return sodium.divide(BigDecimal.valueOf(servings), 2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static class Builder {
        private int recipeId;
        private int calories;
        private BigDecimal protein = BigDecimal.ZERO;
        private BigDecimal carbs = BigDecimal.ZERO;
        private BigDecimal fat = BigDecimal.ZERO;
        private BigDecimal fiber = BigDecimal.ZERO;
        private BigDecimal sugar = BigDecimal.ZERO;
        private BigDecimal sodium = BigDecimal.ZERO;
        
        public Builder recipeId(int recipeId) { this.recipeId = recipeId; return this; }
        public Builder calories(int calories) { this.calories = calories; return this; }
        public Builder protein(BigDecimal protein) { this.protein = protein; return this; }
        public Builder carbs(BigDecimal carbs) { this.carbs = carbs; return this; }
        public Builder fat(BigDecimal fat) { this.fat = fat; return this; }
        public Builder fiber(BigDecimal fiber) { this.fiber = fiber; return this; }
        public Builder sugar(BigDecimal sugar) { this.sugar = sugar; return this; }
        public Builder sodium(BigDecimal sodium) { this.sodium = sodium; return this; }
        
        public Nutrition build() {
            return new Nutrition(recipeId, calories, protein, carbs, fat, fiber, sugar, sodium);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}