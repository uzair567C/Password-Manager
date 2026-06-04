package com.recipe.models;

import java.math.BigDecimal;
// import java.math.RoundingMode; unused import error

public record Ingredient(
    int id,
    String name,
    String unit,
    BigDecimal unitPrice,
    BigDecimal caloriesPerUnit,
    BigDecimal proteinPerUnit,
    BigDecimal carbsPerUnit,
    BigDecimal fatPerUnit
) {
    
    public static final Ingredient EMPTY = new Ingredient(0, "", "", BigDecimal.ZERO, 
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    
    public double getUnitPriceDouble() {
        return unitPrice != null ? unitPrice.doubleValue() : 0.0;
    }
    
    public double getCaloriesPerUnitDouble() {
        return caloriesPerUnit != null ? caloriesPerUnit.doubleValue() : 0.0;
    }
    
    @Override
    public String toString() {
        return name + " (" + unit + ")";
    }
    
    public static class Builder {
        private int id;
        private String name;
        private String unit;
        private BigDecimal unitPrice = BigDecimal.ZERO;
        private BigDecimal caloriesPerUnit = BigDecimal.ZERO;
        private BigDecimal proteinPerUnit = BigDecimal.ZERO;
        private BigDecimal carbsPerUnit = BigDecimal.ZERO;
        private BigDecimal fatPerUnit = BigDecimal.ZERO;
        
        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder caloriesPerUnit(BigDecimal caloriesPerUnit) { this.caloriesPerUnit = caloriesPerUnit; return this; }
        public Builder proteinPerUnit(BigDecimal proteinPerUnit) { this.proteinPerUnit = proteinPerUnit; return this; }
        public Builder carbsPerUnit(BigDecimal carbsPerUnit) { this.carbsPerUnit = carbsPerUnit; return this; }
        public Builder fatPerUnit(BigDecimal fatPerUnit) { this.fatPerUnit = fatPerUnit; return this; }
        
        public Ingredient build() {
            return new Ingredient(id, name, unit, unitPrice, caloriesPerUnit, 
                proteinPerUnit, carbsPerUnit, fatPerUnit);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}