package com.recipe.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.recipe.models.RecipeIngredient;

public class CostEstimator {
    
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    
    public double calculateTotalCost(List<RecipeIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        
        BigDecimal total = ingredients.stream()
            .map(RecipeIngredient::getCost)
            .filter(cost -> cost != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return total.setScale(SCALE, ROUNDING).doubleValue();
    }
    
    public double calculateCostPerServing(double totalCost, int servings) {
        if (servings <= 0) return totalCost;
        return BigDecimal.valueOf(totalCost)
            .divide(BigDecimal.valueOf(servings), SCALE, ROUNDING)
            .doubleValue();
    }
    
    public BigDecimal calculateProfitMargin(double sellingPrice, double totalCost) {
        if (sellingPrice <= 0 || totalCost <= 0) return BigDecimal.ZERO;
        
        BigDecimal selling = BigDecimal.valueOf(sellingPrice);
        BigDecimal cost = BigDecimal.valueOf(totalCost);
        
        return selling.subtract(cost)
            .divide(selling, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, ROUNDING);
    }
    
    public BigDecimal calculateSuggestedSellingPrice(double totalCost, double desiredMarginPercent) {
        if (desiredMarginPercent <= 0 || desiredMarginPercent >= 100) {
            return BigDecimal.valueOf(totalCost);
        }
        
        BigDecimal cost = BigDecimal.valueOf(totalCost);
        BigDecimal marginFactor = BigDecimal.valueOf(100)
            .divide(BigDecimal.valueOf(100 - desiredMarginPercent), 4, ROUNDING);
        
        return cost.multiply(marginFactor).setScale(2, ROUNDING);
    }
}