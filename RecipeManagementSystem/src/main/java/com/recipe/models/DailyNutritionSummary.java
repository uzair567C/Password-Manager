package com.recipe.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregated nutrition summary for a single day.
 */
public record DailyNutritionSummary(
    LocalDate date,
    int totalCalories,
    BigDecimal totalProtein,
    BigDecimal totalCarbs,
    BigDecimal totalFat,
    int calorieGoal,
    boolean isGoalMet
) {
    
    public int getRemainingCalories() {
        return Math.max(0, calorieGoal - totalCalories);
    }
    
    public int getCaloriePercentage() {
        if (calorieGoal <= 0) return 0;
        return (int) Math.min(100, ((double) totalCalories / calorieGoal) * 100);
    }
    
    public double getProteinPercentage() {
        // Assuming 10-35% of calories from protein is healthy
        // 1g protein = 4 calories
        if (totalProtein == null || totalCalories <= 0) return 0;
        int proteinCalories = totalProtein.multiply(BigDecimal.valueOf(4)).intValue();
        return Math.min(100, ((double) proteinCalories / totalCalories) * 100);
    }
    
    public double getCarbsPercentage() {
        // Assuming 45-65% of calories from carbs is healthy
        if (totalCarbs == null || totalCalories <= 0) return 0;
        int carbCalories = totalCarbs.multiply(BigDecimal.valueOf(4)).intValue();
        return Math.min(100, ((double) carbCalories / totalCalories) * 100);
    }
    
    public double getFatPercentage() {
        // Assuming 20-35% of calories from fat is healthy
        if (totalFat == null || totalCalories <= 0) return 0;
        int fatCalories = totalFat.multiply(BigDecimal.valueOf(9)).intValue();
        return Math.min(100, ((double) fatCalories / totalCalories) * 100);
    }
    
    public static class Builder {
        private LocalDate date;
        private int totalCalories = 0;
        private BigDecimal totalProtein = BigDecimal.ZERO;
        private BigDecimal totalCarbs = BigDecimal.ZERO;
        private BigDecimal totalFat = BigDecimal.ZERO;
        private int calorieGoal = 2000;
        
        public Builder date(LocalDate date) { 
            this.date = date; 
            return this; 
        }
        
        public Builder totalCalories(int totalCalories) { 
            this.totalCalories = totalCalories; 
            return this; 
        }
        
        public Builder totalProtein(BigDecimal totalProtein) { 
            this.totalProtein = totalProtein; 
            return this; 
        }
        
        public Builder totalCarbs(BigDecimal totalCarbs) { 
            this.totalCarbs = totalCarbs; 
            return this; 
        }
        
        public Builder totalFat(BigDecimal totalFat) { 
            this.totalFat = totalFat; 
            return this; 
        }
        
        public Builder calorieGoal(int calorieGoal) { 
            this.calorieGoal = calorieGoal; 
            return this; 
        }
        
        public DailyNutritionSummary build() {
            boolean isGoalMet = totalCalories <= calorieGoal;
            return new DailyNutritionSummary(
                date, 
                totalCalories, 
                totalProtein, 
                totalCarbs, 
                totalFat, 
                calorieGoal, 
                isGoalMet
            );
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}