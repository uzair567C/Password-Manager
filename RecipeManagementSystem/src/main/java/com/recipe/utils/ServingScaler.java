package com.recipe.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.recipe.models.Nutrition;
import com.recipe.models.RecipeIngredient;

public class ServingScaler {
    
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    
    public List<RecipeIngredient> scaleIngredients(List<RecipeIngredient> ingredients, 
                                                    int originalServings, int newServings) {
        if (originalServings == newServings) {
            return new ArrayList<>(ingredients);
        }
        
        BigDecimal scaleFactor = BigDecimal.valueOf(newServings)
            .divide(BigDecimal.valueOf(originalServings), SCALE, ROUNDING);
        
        List<RecipeIngredient> scaledIngredients = new ArrayList<>();
        
        for (RecipeIngredient ingredient : ingredients) {
            BigDecimal scaledQuantity = ingredient.quantity()
                .multiply(scaleFactor)
                .setScale(2, RoundingMode.HALF_UP);
            
            RecipeIngredient scaled = RecipeIngredient.builder()
                .recipeId(ingredient.recipeId())
                .ingredient(ingredient.ingredient())
                .quantity(scaledQuantity)
                .unit(ingredient.unit())
                .notes(ingredient.notes())
                .build();
            
            scaledIngredients.add(scaled);
        }
        
        return scaledIngredients;
    }
    
    public Nutrition scaleNutrition(Nutrition nutrition, int originalServings, int newServings) {
        if (nutrition == null || originalServings == newServings) {
            return nutrition;
        }
        
        BigDecimal scaleFactor = BigDecimal.valueOf(newServings)
            .divide(BigDecimal.valueOf(originalServings), SCALE, ROUNDING);
        
        return Nutrition.builder()
            .recipeId(nutrition.recipeId())
            .calories((int) Math.round(nutrition.calories() * scaleFactor.doubleValue()))
            .protein(nutrition.protein().multiply(scaleFactor).setScale(2, ROUNDING))
            .carbs(nutrition.carbs().multiply(scaleFactor).setScale(2, ROUNDING))
            .fat(nutrition.fat().multiply(scaleFactor).setScale(2, ROUNDING))
            .fiber(nutrition.fiber() != null ? nutrition.fiber().multiply(scaleFactor).setScale(2, ROUNDING) : null)
            .sugar(nutrition.sugar() != null ? nutrition.sugar().multiply(scaleFactor).setScale(2, ROUNDING) : null)
            .sodium(nutrition.sodium() != null ? nutrition.sodium().multiply(scaleFactor).setScale(2, ROUNDING) : null)
            .build();
    }
    
    public int scaleTime(int originalTime, int originalServings, int newServings) {
        if (originalServings == newServings || originalTime == 0) {
            return originalTime;
        }
        
        // Time doesn't scale linearly - using a logarithmic approach
        double ratio = (double) newServings / originalServings;
        double scaled = originalTime * Math.pow(ratio, 0.7); // Sub-linear scaling
        return (int) Math.max(1, Math.round(scaled));
    }
}