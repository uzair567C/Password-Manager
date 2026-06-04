package com.recipe.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record RecipeIngredient(
    int recipeId,
    Ingredient ingredient,
    BigDecimal quantity,
    String unit,
    String notes
) {
    
    public BigDecimal getTotalCalories() {
        if (ingredient.caloriesPerUnit() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.caloriesPerUnit().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTotalProtein() {
        if (ingredient.proteinPerUnit() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.proteinPerUnit().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTotalCarbs() {
        if (ingredient.carbsPerUnit() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.carbsPerUnit().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTotalFat() {
        if (ingredient.fatPerUnit() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.fatPerUnit().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getCost() {
        if (ingredient.unitPrice() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return ingredient.unitPrice().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }
    
    public String getFormattedQuantity() {
        if (quantity == null) return "0";
        BigDecimal qty = quantity.stripTrailingZeros();
        return qty.toPlainString() + " " + unit;
    }
    
    public static class Builder {
        private int recipeId;
        private Ingredient ingredient;
        private BigDecimal quantity;
        private String unit;
        private String notes;
        
        public Builder recipeId(int recipeId) { this.recipeId = recipeId; return this; }
        public Builder ingredient(Ingredient ingredient) { this.ingredient = ingredient; return this; }
        public Builder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        
        public RecipeIngredient build() {
            return new RecipeIngredient(recipeId, ingredient, quantity, unit, notes);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}