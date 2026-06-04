package com.recipe.services;

import java.time.LocalDate;
import java.util.List;

import com.recipe.auth.SessionManager;
import com.recipe.dao.FavouriteDAO;
import com.recipe.dao.MealPlanDAO;
import com.recipe.dao.RatingDAO;
import com.recipe.dao.RecipeDAO;
import com.recipe.dao.UserDAO;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.DashboardStats;
import com.recipe.models.Recipe;

public class DashboardService {
    
    private final RecipeDAO recipeDAO;
    private final RatingDAO ratingDAO;
    private final FavouriteDAO favouriteDAO;
    private final MealPlanDAO mealPlanDAO;
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    
    public DashboardService() {
        this.recipeDAO = new RecipeDAO();
        this.ratingDAO = new RatingDAO();
        this.favouriteDAO = new FavouriteDAO();
        this.mealPlanDAO = new MealPlanDAO();
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public DashboardStats getDashboardStats() throws DatabaseException {
        List<Recipe> allRecipes = recipeDAO.getAllRecipes();
        List<Recipe> topRated = getTopRatedRecipes();
        List<Recipe> mostCooked = getMostCookedRecipes();
        
        double avgRating = allRecipes.stream()
            .mapToDouble(Recipe::averageRating)
            .average()
            .orElse(0.0);
        
        int totalCookCount = allRecipes.stream()
            .mapToInt(Recipe::cookCount)
            .sum();
        
        LocalDate weekStart = getStartOfWeek(LocalDate.now());
        List<com.recipe.dao.MealPlanDAO.PlannedRecipeInfo> weekPlans = 
            mealPlanDAO.getPlannedRecipesForWeek(sessionManager.getCurrentUserId(), weekStart);
        
        List<Integer> favourites = favouriteDAO.getUserFavourites(sessionManager.getCurrentUserId());
        
        int totalUsers = userDAO.getAllUsers().size();
        
        return DashboardStats.builder()
            .totalRecipes(allRecipes.size())
            .totalUsers(totalUsers)
            .totalMealsPlanned(weekPlans.size())
            .averageRating(avgRating)
            .topRatedRecipe(topRated.isEmpty() ? null : topRated.get(0))
            .mostCookedRecipe(mostCooked.isEmpty() ? null : mostCooked.get(0))
            .totalCookCount(totalCookCount)
            .thisWeekPlans(weekPlans.size())
            .weeklyCalorieAverage(calculateWeeklyCalorieAverage(weekStart))
            .favouriteCount(favourites.size())
            .build();
    }
    
    private List<Recipe> getTopRatedRecipes() throws DatabaseException {
        List<Recipe> allRecipes = recipeDAO.getAllRecipes();
        return allRecipes.stream()
            .filter(r -> r.averageRating() > 0)
            .sorted((r1, r2) -> Double.compare(r2.averageRating(), r1.averageRating()))
            .limit(5)
            .toList();
    }
    
    private List<Recipe> getMostCookedRecipes() throws DatabaseException {
        List<Recipe> allRecipes = recipeDAO.getAllRecipes();
        return allRecipes.stream()
            .filter(r -> r.cookCount() > 0)
            .sorted((r1, r2) -> Integer.compare(r2.cookCount(), r1.cookCount()))
            .limit(5)
            .toList();
    }
    
    private double calculateWeeklyCalorieAverage(LocalDate weekStart) throws DatabaseException {
        List<com.recipe.dao.MealPlanDAO.PlannedRecipeInfo> weekPlans = 
            mealPlanDAO.getPlannedRecipesForWeek(sessionManager.getCurrentUserId(), weekStart);
        
        if (weekPlans.isEmpty()) {
            return 0.0;
        }
        
        int totalCalories = 0;
        int recipeCount = 0;
        
        for (com.recipe.dao.MealPlanDAO.PlannedRecipeInfo info : weekPlans) {
            try {
                java.util.Optional<Recipe> recipeOpt = recipeDAO.getRecipeById(info.recipeId());
                if (recipeOpt.isPresent() && recipeOpt.get().nutrition() != null) {
                    totalCalories += recipeOpt.get().nutrition().calories();
                    recipeCount++;
                }
            } catch (DatabaseException e) {
                // Skip this recipe
            }
        }
        
        return recipeCount > 0 ? (double) totalCalories / recipeCount : 0.0;
    }
    
    private LocalDate getStartOfWeek(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }
}