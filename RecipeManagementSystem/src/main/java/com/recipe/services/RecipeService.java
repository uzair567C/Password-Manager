package com.recipe.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.recipe.auth.SessionManager;
import com.recipe.dao.IngredientDAO;
import com.recipe.dao.RecipeDAO;
import com.recipe.exceptions.DatabaseException;
import com.recipe.exceptions.ValidationException;
import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;
import com.recipe.utils.CostEstimator;
import com.recipe.utils.ServingScaler;

public class RecipeService {
    
    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;
    private final SessionManager sessionManager;
    private final ServingScaler servingScaler;
    private final CostEstimator costEstimator;
    
    public RecipeService() {
        this.recipeDAO = new RecipeDAO();
        this.ingredientDAO = new IngredientDAO();
        this.sessionManager = SessionManager.getInstance();
        this.servingScaler = new ServingScaler();
        this.costEstimator = new CostEstimator();
    }
    
    public Recipe createRecipe(Recipe recipe) throws ValidationException, DatabaseException {
        validateRecipe(recipe);
        
        int currentUserId = sessionManager.getCurrentUserId();
        
        Recipe finalRecipe = Recipe.builder()
            .userId(currentUserId)
            .title(recipe.title())
            .description(recipe.description())
            .prepTime(recipe.prepTime())
            .cookTime(recipe.cookTime())
            .servings(recipe.servings())
            .difficulty(recipe.difficulty())
            .categoryId(recipe.categoryId())
            .photoPath(recipe.photoPath())
            .cost(calculateTotalCost(recipe.ingredients()))
            .isDraft(recipe.isDraft())
            .ingredients(recipe.ingredients())
            .nutrition(recipe.nutrition())
            .build();
        
        return recipeDAO.insertRecipe(finalRecipe);
    }
    
    public boolean updateRecipe(Recipe recipe) throws ValidationException, DatabaseException {
        validateRecipe(recipe);
        
        int currentUserId = sessionManager.getCurrentUserId();
        
        // Check if recipe exists and belongs to current user
        Optional<Recipe> existing = recipeDAO.getRecipeByIdAndUser(recipe.id(), currentUserId);
        if (existing.isEmpty()) {
            throw new ValidationException("Recipe not found or you don't have permission to edit it");
        }
        
        Recipe updatedRecipe = Recipe.builder()
            .id(recipe.id())
            .userId(currentUserId)
            .title(recipe.title())
            .description(recipe.description())
            .prepTime(recipe.prepTime())
            .cookTime(recipe.cookTime())
            .servings(recipe.servings())
            .difficulty(recipe.difficulty())
            .categoryId(recipe.categoryId())
            .photoPath(recipe.photoPath())
            .cost(calculateTotalCost(recipe.ingredients()))
            .cookCount(existing.get().cookCount())
            .averageRating(existing.get().averageRating())
            .isDraft(recipe.isDraft())
            .ingredients(recipe.ingredients())
            .nutrition(recipe.nutrition())
            .build();
        
        return recipeDAO.updateRecipe(updatedRecipe);
    }
    
    public Optional<Recipe> getRecipeById(int id) throws DatabaseException {
        return recipeDAO.getRecipeById(id);
    }
    
    public Optional<Recipe> getMyRecipeById(int id) throws DatabaseException {
        int currentUserId = sessionManager.getCurrentUserId();
        return recipeDAO.getRecipeByIdAndUser(id, currentUserId);
    }
    
    public List<Recipe> getAllRecipes() throws DatabaseException {
        return recipeDAO.getAllRecipes();
    }
    
    public List<Recipe> getMyRecipes() throws DatabaseException {
        return recipeDAO.getRecipesByUser(sessionManager.getCurrentUserId());
    }
    
    public boolean deleteRecipe(int id) throws DatabaseException, ValidationException {
        int currentUserId = sessionManager.getCurrentUserId();
        
        Optional<Recipe> recipe = recipeDAO.getRecipeByIdAndUser(id, currentUserId);
        if (recipe.isEmpty()) {
            throw new ValidationException("Recipe not found or you don't have permission to delete it");
        }
        
        return recipeDAO.deleteRecipe(id, currentUserId);
    }
    
    public Recipe duplicateRecipe(int id) throws DatabaseException, ValidationException {
        int currentUserId = sessionManager.getCurrentUserId();
        
        Optional<Recipe> original = recipeDAO.getRecipeByIdAndUser(id, currentUserId);
        if (original.isEmpty()) {
            throw new ValidationException("Recipe not found or you don't have permission to duplicate it");
        }
        
        Recipe duplicate = Recipe.builder()
            .userId(currentUserId)
            .title(original.get().title() + " (Copy)")
            .description(original.get().description())
            .prepTime(original.get().prepTime())
            .cookTime(original.get().cookTime())
            .servings(original.get().servings())
            .difficulty(original.get().difficulty())
            .categoryId(original.get().categoryId())
            .photoPath(original.get().photoPath())
            .cost(original.get().cost())
            .isDraft(true)
            .ingredients(original.get().ingredients())
            .nutrition(original.get().nutrition())
            .build();
        
        return recipeDAO.insertRecipe(duplicate);
    }
    
    public Recipe scaleRecipe(Recipe recipe, int newServings) {
        if (newServings <= 0 || newServings == recipe.servings()) {
            return recipe;
        }
        
        List<RecipeIngredient> scaledIngredients = servingScaler.scaleIngredients(
            recipe.ingredients(), recipe.servings(), newServings);
        
        double newCost = costEstimator.calculateTotalCost(scaledIngredients);
        Nutrition scaledNutrition = servingScaler.scaleNutrition(recipe.nutrition(), 
            recipe.servings(), newServings);
        
        return Recipe.builder()
            .id(recipe.id())
            .userId(recipe.userId())
            .title(recipe.title())
            .description(recipe.description())
            .prepTime(recipe.prepTime())
            .cookTime(recipe.cookTime())
            .servings(newServings)
            .difficulty(recipe.difficulty())
            .categoryId(recipe.categoryId())
            .photoPath(recipe.photoPath())
            .cost(newCost)
            .cookCount(recipe.cookCount())
            .averageRating(recipe.averageRating())
            .isDraft(recipe.isDraft())
            .ingredients(scaledIngredients)
            .nutrition(scaledNutrition)
            .build();
    }
    
    public void recordCook(int recipeId) throws DatabaseException {
        recipeDAO.incrementCookCount(recipeId);
    }
    
    public List<Recipe> searchMyRecipes(String searchTerm) throws DatabaseException {
        return recipeDAO.searchByTitleForUser(sessionManager.getCurrentUserId(), searchTerm);
    }
    
    private void validateRecipe(Recipe recipe) throws ValidationException {
        if (recipe.title() == null || recipe.title().isBlank()) {
            throw new ValidationException("Recipe title is required");
        }
        
        if (recipe.title().length() > 200) {
            throw new ValidationException("Recipe title must be less than 200 characters");
        }
        
        if (recipe.prepTime() < 0) {
            throw new ValidationException("Prep time cannot be negative");
        }
        
        if (recipe.cookTime() < 0) {
            throw new ValidationException("Cook time cannot be negative");
        }
        
        if (recipe.servings() < 1) {
            throw new ValidationException("Servings must be at least 1");
        }
        
        if (recipe.servings() > 100) {
            throw new ValidationException("Servings cannot exceed 100");
        }
        
        if (recipe.ingredients() == null || recipe.ingredients().isEmpty()) {
            throw new ValidationException("Recipe must have at least one ingredient");
        }
    }
    
    private double calculateTotalCost(List<RecipeIngredient> ingredients) {
        BigDecimal total = ingredients.stream()
            .map(RecipeIngredient::getCost)
            .filter(cost -> cost != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.doubleValue();
    }
}