package com.recipe.models;

import java.math.BigDecimal;
// import java.math.RoundingMode;  unused import error

/**
 * Represents an item in the grocery list with aggregated quantities from multiple recipes.
 */
public record GroceryItem(
    int ingredientId,
    String ingredientName,
    BigDecimal totalQuantity,
    String unit,
    boolean isPurchased
) {
    
    public String getFormattedQuantity() {
        if (totalQuantity == null) return "0 " + unit;
        BigDecimal qty = totalQuantity.stripTrailingZeros();
        return qty.toPlainString() + " " + unit;
    }
    
    public GroceryItem markPurchased() {
        return new GroceryItem(ingredientId, ingredientName, totalQuantity, unit, true);
    }
    
    public GroceryItem markNotPurchased() {
        return new GroceryItem(ingredientId, ingredientName, totalQuantity, unit, false);
    }
    
    public static class Builder {
        private int ingredientId;
        private String ingredientName;
        private BigDecimal totalQuantity = BigDecimal.ZERO;
        private String unit;
        private boolean isPurchased = false;
        
        public Builder ingredientId(int ingredientId) { this.ingredientId = ingredientId; return this; }
        public Builder ingredientName(String ingredientName) { this.ingredientName = ingredientName; return this; }
        public Builder totalQuantity(BigDecimal totalQuantity) { this.totalQuantity = totalQuantity; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder isPurchased(boolean isPurchased) { this.isPurchased = isPurchased; return this; }
        
        public GroceryItem build() {
            return new GroceryItem(ingredientId, ingredientName, totalQuantity, unit, isPurchased);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}