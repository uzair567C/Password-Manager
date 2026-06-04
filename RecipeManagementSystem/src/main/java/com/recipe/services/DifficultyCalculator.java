package com.recipe.services;

import com.recipe.models.Recipe;

public class DifficultyCalculator {
    
    public String calculateDifficulty(Recipe recipe) {
        int score = 0;
        
        // Factor 1: Number of ingredients
        int ingredientCount = recipe.ingredients() != null ? recipe.ingredients().size() : 0;
        if (ingredientCount > 10) score += 3;
        else if (ingredientCount > 6) score += 2;
        else if (ingredientCount > 3) score += 1;
        
        // Factor 2: Total cooking time
        int totalTime = recipe.prepTime() + recipe.cookTime();
        if (totalTime > 120) score += 3;
        else if (totalTime > 60) score += 2;
        else if (totalTime > 30) score += 1;
        
        // Factor 3: Steps count (if available)
        // This would come from recipe steps
        
        // Determine difficulty
        if (score <= 2) return "Easy";
        if (score <= 4) return "Medium";
        return "Hard";
    }
    
    public String getDifficultyIcon(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "easy" -> "🍀";
            case "medium" -> "⭐";
            case "hard" -> "🔥";
            default -> "❓";
        };
    }
    
    public java.awt.Color getDifficultyColor(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "easy" -> new java.awt.Color(46, 204, 113);
            case "medium" -> new java.awt.Color(241, 196, 15);
            case "hard" -> new java.awt.Color(231, 76, 60);
            default -> new java.awt.Color(150, 150, 150);
        };
    }
}