package com.recipe.models;

/**
 * Dashboard statistics summary.
 */
public record DashboardStats(
    int totalRecipes,
    int totalUsers,
    int totalMealsPlanned,
    double averageRating,
    Recipe topRatedRecipe,
    Recipe mostCookedRecipe,
    int totalCookCount,
    int thisWeekPlans,
    double weeklyCalorieAverage,
    int favouriteCount
) {
    
    public static class Builder {
        private int totalRecipes;
        private int totalUsers;
        private int totalMealsPlanned;
        private double averageRating;
        private Recipe topRatedRecipe;
        private Recipe mostCookedRecipe;
        private int totalCookCount;
        private int thisWeekPlans;
        private double weeklyCalorieAverage;
        private int favouriteCount;
        
        public Builder totalRecipes(int totalRecipes) { this.totalRecipes = totalRecipes; return this; }
        public Builder totalUsers(int totalUsers) { this.totalUsers = totalUsers; return this; }
        public Builder totalMealsPlanned(int totalMealsPlanned) { this.totalMealsPlanned = totalMealsPlanned; return this; }
        public Builder averageRating(double averageRating) { this.averageRating = averageRating; return this; }
        public Builder topRatedRecipe(Recipe topRatedRecipe) { this.topRatedRecipe = topRatedRecipe; return this; }
        public Builder mostCookedRecipe(Recipe mostCookedRecipe) { this.mostCookedRecipe = mostCookedRecipe; return this; }
        public Builder totalCookCount(int totalCookCount) { this.totalCookCount = totalCookCount; return this; }
        public Builder thisWeekPlans(int thisWeekPlans) { this.thisWeekPlans = thisWeekPlans; return this; }
        public Builder weeklyCalorieAverage(double weeklyCalorieAverage) { this.weeklyCalorieAverage = weeklyCalorieAverage; return this; }
        public Builder favouriteCount(int favouriteCount) { this.favouriteCount = favouriteCount; return this; }
        
        public DashboardStats build() {
            return new DashboardStats(totalRecipes, totalUsers, totalMealsPlanned, averageRating,
                topRatedRecipe, mostCookedRecipe, totalCookCount, thisWeekPlans,
                weeklyCalorieAverage, favouriteCount);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}