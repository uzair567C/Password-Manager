package com.recipe.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.recipe.auth.SessionManager;
import com.recipe.dao.MealPlanDAO;
import com.recipe.dao.NutritionDAO;
import com.recipe.dao.RecipeDAO;
import com.recipe.exceptions.DatabaseException;
import com.recipe.exceptions.ValidationException;
import com.recipe.models.DailyNutritionSummary;
import com.recipe.models.GroceryItem;
import com.recipe.models.MealPlan;
import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.User;

public class MealPlanService {
    
    private final MealPlanDAO mealPlanDAO;
    private final RecipeDAO recipeDAO;
    private final NutritionDAO nutritionDAO;
    private final SessionManager sessionManager;
    private final GroceryListBuilder groceryListBuilder;
    
    // Helper class to accumulate nutrition values
    private static class NutritionAccumulator {
        int totalCalories = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        
        void add(Nutrition nutrition) {
            this.totalCalories += nutrition.calories();
            this.totalProtein = this.totalProtein.add(nutrition.protein());
            this.totalCarbs = this.totalCarbs.add(nutrition.carbs());
            this.totalFat = this.totalFat.add(nutrition.fat());
        }
    }
    
    public MealPlanService() {
        this.mealPlanDAO = new MealPlanDAO();
        this.recipeDAO = new RecipeDAO();
        this.nutritionDAO = new NutritionDAO();
        this.sessionManager = SessionManager.getInstance();
        this.groceryListBuilder = new GroceryListBuilder();
    }
    
    public MealPlan assignRecipeToMeal(int recipeId, int dayOfWeek, String mealType, LocalDate weekStart) 
            throws DatabaseException, ValidationException {
        
        if (recipeId <= 0) {
            throw new ValidationException("Please select a valid recipe");
        }
        
        Optional<Recipe> recipe = recipeDAO.getRecipeById(recipeId);
        if (recipe.isEmpty()) {
            throw new ValidationException("Recipe not found");
        }
        
        MealPlan mealPlan = MealPlan.builder()
            .userId(sessionManager.getCurrentUserId())
            .recipeId(recipeId)
            .dayOfWeek(dayOfWeek)
            .mealType(mealType)
            .weekStart(weekStart)
            .recipeTitle(recipe.get().title())
            .recipePhotoPath(recipe.get().photoPath())
            .build();
        
        return mealPlanDAO.saveMealPlan(mealPlan);
    }
    
    public boolean removeMealPlan(int dayOfWeek, String mealType, LocalDate weekStart) throws DatabaseException {
        return mealPlanDAO.deleteMealPlan(sessionManager.getCurrentUserId(), dayOfWeek, mealType, weekStart);
    }
    
    public List<MealPlan> getWeekPlan(LocalDate weekStart) throws DatabaseException {
        return mealPlanDAO.getWeekPlans(sessionManager.getCurrentUserId(), weekStart);
    }
    
    public void clearWeekPlan(LocalDate weekStart) throws DatabaseException {
        mealPlanDAO.clearWeekPlans(sessionManager.getCurrentUserId(), weekStart);
    }
    
    public List<GroceryItem> generateGroceryList(LocalDate weekStart) throws DatabaseException {
        List<MealPlanDAO.PlannedRecipeInfo> plannedRecipes = 
            mealPlanDAO.getPlannedRecipesForWeek(sessionManager.getCurrentUserId(), weekStart);
        
        List<Recipe> recipes = new ArrayList<>();
        for (MealPlanDAO.PlannedRecipeInfo info : plannedRecipes) {
            Optional<Recipe> recipe = recipeDAO.getRecipeById(info.recipeId());
            recipe.ifPresent(recipes::add);
        }
        
        return groceryListBuilder.buildGroceryList(recipes);
    }
    
    public Map<LocalDate, DailyNutritionSummary> getWeeklyNutritionSummary(LocalDate weekStart) 
            throws DatabaseException {
        
        List<MealPlan> weekPlans = getWeekPlan(weekStart);
        User currentUser = sessionManager.getCurrentUser();
        int calorieGoal = currentUser != null ? currentUser.getCalorieGoal() : 2000;
        
        // Use accumulator map instead of builder map
        Map<LocalDate, NutritionAccumulator> accumulatorMap = new HashMap<>();
        
        // Initialize accumulators for each day of the week
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            accumulatorMap.put(date, new NutritionAccumulator());
        }
        
        // Aggregate nutrition for each meal plan
        for (MealPlan plan : weekPlans) {
            LocalDate date = plan.getDate();
            Optional<Nutrition> nutritionOpt = nutritionDAO.getNutritionByRecipeId(plan.recipeId());
            
            if (nutritionOpt.isPresent()) {
                Nutrition nutrition = nutritionOpt.get();
                NutritionAccumulator accumulator = accumulatorMap.get(date);
                
                if (accumulator != null) {
                    accumulator.add(nutrition);
                }
            }
        }
        
        // Build final summaries from accumulators
        Map<LocalDate, DailyNutritionSummary> result = new HashMap<>();
        for (Map.Entry<LocalDate, NutritionAccumulator> entry : accumulatorMap.entrySet()) {
            LocalDate date = entry.getKey();
            NutritionAccumulator acc = entry.getValue();
            
            DailyNutritionSummary summary = DailyNutritionSummary.builder()
                .date(date)
                .totalCalories(acc.totalCalories)
                .totalProtein(acc.totalProtein)
                .totalCarbs(acc.totalCarbs)
                .totalFat(acc.totalFat)
                .calorieGoal(calorieGoal)
                .build();
            
            result.put(date, summary);
        }
        
        return result;
    }
    
    public boolean canCopyWeekPlan(LocalDate sourceWeek, LocalDate targetWeek) throws DatabaseException {
        List<MealPlan> sourcePlans = getWeekPlan(sourceWeek);
        return !sourcePlans.isEmpty();
    }
    
    public void copyWeekPlan(LocalDate sourceWeek, LocalDate targetWeek) throws DatabaseException {
        List<MealPlan> sourcePlans = getWeekPlan(sourceWeek);
        
        for (MealPlan plan : sourcePlans) {
            try {
                assignRecipeToMeal(plan.recipeId(), plan.dayOfWeek(), plan.mealType(), targetWeek);
            } catch (ValidationException e) {
                // Log and continue - this shouldn't happen for valid recipes
                System.err.println("Failed to copy meal plan: " + e.getMessage());
            }
        }
    }
    
    public Map<String, Integer> getMostPlannedRecipes(int limit) throws DatabaseException {
        // This would be a more complex query, simplified for now
        return new HashMap<>();
    }
}