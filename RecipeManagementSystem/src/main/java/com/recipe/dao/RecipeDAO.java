package com.recipe.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.Ingredient;
import com.recipe.models.Nutrition;
import com.recipe.models.Recipe;
import com.recipe.models.RecipeIngredient;

public class RecipeDAO {
    
    private final DatabaseConnection dbConnection;
    private final IngredientDAO ingredientDAO;
    
    private static final String INSERT_RECIPE_SQL = """
        INSERT INTO recipes (user_id, title, description, prep_time, cook_time, servings, 
                             difficulty, category_id, photo_path, cost, is_draft)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id, created_at, updated_at
        """;
    
    private static final String UPDATE_RECIPE_SQL = """
        UPDATE recipes SET title = ?, description = ?, prep_time = ?, cook_time = ?, 
                           servings = ?, difficulty = ?, category_id = ?, photo_path = ?, 
                           cost = ?, is_draft = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND user_id = ?
        """;
    
    private static final String SELECT_RECIPE_BY_ID_SQL = """
        SELECT r.*, 
               COALESCE(AVG(rat.stars), 0) as avg_rating,
               COUNT(DISTINCT rat.id) as rating_count
        FROM recipes r
        LEFT JOIN ratings rat ON r.id = rat.recipe_id
        WHERE r.id = ? AND r.is_draft = false
        GROUP BY r.id
        """;
    
    private static final String SELECT_ALL_RECIPES_SQL = """
        SELECT r.*, 
               COALESCE(AVG(rat.stars), 0) as avg_rating,
               COUNT(DISTINCT rat.id) as rating_count
        FROM recipes r
        LEFT JOIN ratings rat ON r.id = rat.recipe_id
        WHERE r.is_draft = false
        GROUP BY r.id
        ORDER BY r.created_at DESC
        """;
    
    private static final String SELECT_RECIPES_BY_USER_SQL = """
        SELECT r.*, 
               COALESCE(AVG(rat.stars), 0) as avg_rating
        FROM recipes r
        LEFT JOIN ratings rat ON r.id = rat.recipe_id
        WHERE r.user_id = ? AND r.is_draft = false
        GROUP BY r.id
        ORDER BY r.created_at DESC
        """;
    
    private static final String DELETE_RECIPE_SQL = "DELETE FROM recipes WHERE id = ? AND user_id = ?";
    private static final String INCREMENT_COOK_COUNT_SQL = "UPDATE recipes SET cook_count = cook_count + 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    private static final String SELECT_RECIPES_BY_USER_FOR_SEARCH_SQL = """
        SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
        FROM recipes r
        LEFT JOIN ratings rat ON r.id = rat.recipe_id
        WHERE r.user_id = ? AND r.title ILIKE ? AND r.is_draft = false
        GROUP BY r.id
        ORDER BY r.created_at DESC
        """;
    
    private static final String SELECT_RECIPE_BY_ID_AND_USER_SQL = """
        SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
        FROM recipes r
        LEFT JOIN ratings rat ON r.id = rat.recipe_id
        WHERE r.id = ? AND r.user_id = ?
        GROUP BY r.id
        """;
    
    private static final String INSERT_RECIPE_INGREDIENT_SQL = """
        INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, notes)
        VALUES (?, ?, ?, ?, ?)
        """;
    
    private static final String DELETE_RECIPE_INGREDIENTS_SQL = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
    private static final String SELECT_RECIPE_INGREDIENTS_SQL = """
        SELECT ri.*, i.name, i.unit as ingredient_unit, i.unit_price, 
               i.calories_per_unit, i.protein_per_unit, i.carbs_per_unit, i.fat_per_unit
        FROM recipe_ingredients ri
        JOIN ingredients i ON ri.ingredient_id = i.id
        WHERE ri.recipe_id = ?
        """;
    
    private static final String INSERT_NUTRITION_SQL = """
        INSERT INTO nutrition (recipe_id, calories, protein, carbs, fat, fiber, sugar, sodium)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (recipe_id) DO UPDATE SET
            calories = EXCLUDED.calories,
            protein = EXCLUDED.protein,
            carbs = EXCLUDED.carbs,
            fat = EXCLUDED.fat,
            fiber = EXCLUDED.fiber,
            sugar = EXCLUDED.sugar,
            sodium = EXCLUDED.sodium
        """;
    
    private static final String SELECT_NUTRITION_SQL = "SELECT * FROM nutrition WHERE recipe_id = ?";
    
    public RecipeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.ingredientDAO = new IngredientDAO();
    }
    
    public Recipe insertRecipe(Recipe recipe) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_RECIPE_SQL)) {
            
            conn.setAutoCommit(false);
            
            try {
                pstmt.setInt(1, recipe.userId());
                pstmt.setString(2, recipe.title());
                pstmt.setString(3, recipe.description());
                pstmt.setInt(4, recipe.prepTime());
                pstmt.setInt(5, recipe.cookTime());
                pstmt.setInt(6, recipe.servings());
                pstmt.setString(7, recipe.difficulty());
                pstmt.setObject(8, recipe.categoryId(), Types.INTEGER);
                pstmt.setString(9, recipe.photoPath());
                pstmt.setBigDecimal(10, BigDecimal.valueOf(recipe.cost()));
                pstmt.setBoolean(11, recipe.isDraft());
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int recipeId = rs.getInt("id");
                        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                        
                        saveIngredients(conn, recipeId, recipe.ingredients());
                        
                        if (recipe.nutrition() != null) {
                            saveNutrition(conn, recipeId, recipe.nutrition());
                        }
                        
                        conn.commit();
                        
                        return loadCompleteRecipe(recipeId);
                    }
                }
                throw new DatabaseException("Failed to insert recipe");
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting recipe: " + e.getMessage(), e);
        }
    }
    
    public boolean updateRecipe(Recipe recipe) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_RECIPE_SQL)) {
            
            conn.setAutoCommit(false);
            
            try {
                pstmt.setString(1, recipe.title());
                pstmt.setString(2, recipe.description());
                pstmt.setInt(3, recipe.prepTime());
                pstmt.setInt(4, recipe.cookTime());
                pstmt.setInt(5, recipe.servings());
                pstmt.setString(6, recipe.difficulty());
                pstmt.setObject(7, recipe.categoryId(), Types.INTEGER);
                pstmt.setString(8, recipe.photoPath());
                pstmt.setBigDecimal(9, BigDecimal.valueOf(recipe.cost()));
                pstmt.setBoolean(10, recipe.isDraft());
                pstmt.setInt(11, recipe.id());
                pstmt.setInt(12, recipe.userId());
                
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    deleteIngredients(conn, recipe.id());
                    saveIngredients(conn, recipe.id(), recipe.ingredients());
                    
                    if (recipe.nutrition() != null) {
                        saveNutrition(conn, recipe.id(), recipe.nutrition());
                    }
                    
                    conn.commit();
                    return true;
                }
                return false;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating recipe: " + e.getMessage(), e);
        }
    }
    
    public Optional<Recipe> getRecipeById(int id) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_SQL)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRecipe(rs, conn, id));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting recipe: " + e.getMessage(), e);
        }
    }
    
    public Optional<Recipe> getRecipeByIdAndUser(int id, int userId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_AND_USER_SQL)) {
            
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRecipe(rs, conn, id));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting recipe: " + e.getMessage(), e);
        }
    }
    
    public List<Recipe> getAllRecipes() throws DatabaseException {
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_RECIPES_SQL)) {
            
            while (rs.next()) {
                int recipeId = rs.getInt("id");
                recipes.add(mapResultSetToRecipe(rs, conn, recipeId));
            }
            return recipes;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting recipes: " + e.getMessage(), e);
        }
    }
    
    public List<Recipe> getRecipesByUser(int userId) throws DatabaseException {
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPES_BY_USER_SQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int recipeId = rs.getInt("id");
                    recipes.add(mapResultSetToRecipe(rs, conn, recipeId));
                }
            }
            return recipes;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user recipes: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteRecipe(int recipeId, int userId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_RECIPE_SQL)) {
            
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting recipe: " + e.getMessage(), e);
        }
    }
    
    public void incrementCookCount(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INCREMENT_COOK_COUNT_SQL)) {
            
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error incrementing cook count: " + e.getMessage(), e);
        }
    }
    
    public List<Recipe> searchByTitleForUser(int userId, String searchTerm) throws DatabaseException {
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPES_BY_USER_FOR_SEARCH_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int recipeId = rs.getInt("id");
                    recipes.add(mapResultSetToRecipe(rs, conn, recipeId));
                }
            }
            return recipes;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error searching recipes: " + e.getMessage(), e);
        }
    }
    
    private Recipe loadCompleteRecipe(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPE_BY_ID_SQL)) {
            
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRecipe(rs, conn, recipeId);
                }
            }
            throw new DatabaseException("Recipe not found with id: " + recipeId);
            
        } catch (SQLException e) {
            throw new DatabaseException("Error loading recipe: " + e.getMessage(), e);
        }
    }
    
    private Recipe mapResultSetToRecipe(ResultSet rs, Connection conn, int recipeId) throws SQLException {
        List<RecipeIngredient> ingredients = loadIngredients(conn, recipeId);
        Nutrition nutrition = loadNutrition(conn, recipeId);
        
        return Recipe.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .prepTime(rs.getInt("prep_time"))
            .cookTime(rs.getInt("cook_time"))
            .servings(rs.getInt("servings"))
            .difficulty(rs.getString("difficulty"))
            .categoryId(getInteger(rs, "category_id"))
            .photoPath(rs.getString("photo_path"))
            .cost(rs.getDouble("cost"))
            .cookCount(rs.getInt("cook_count"))
            .averageRating(rs.getDouble("avg_rating"))
            .isDraft(rs.getBoolean("is_draft"))
            .createdAt(getLocalDateTime(rs, "created_at"))
            .updatedAt(getLocalDateTime(rs, "updated_at"))
            .ingredients(ingredients)
            .nutrition(nutrition)
            .build();
    }
    
    private List<RecipeIngredient> loadIngredients(Connection conn, int recipeId) throws SQLException {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_RECIPE_INGREDIENTS_SQL)) {
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = Ingredient.builder()
                        .id(rs.getInt("ingredient_id"))
                        .name(rs.getString("name"))
                        .unit(rs.getString("ingredient_unit"))
                        .unitPrice(rs.getBigDecimal("unit_price"))
                        .caloriesPerUnit(rs.getBigDecimal("calories_per_unit"))
                        .proteinPerUnit(rs.getBigDecimal("protein_per_unit"))
                        .carbsPerUnit(rs.getBigDecimal("carbs_per_unit"))
                        .fatPerUnit(rs.getBigDecimal("fat_per_unit"))
                        .build();
                    
                    ingredients.add(RecipeIngredient.builder()
                        .recipeId(recipeId)
                        .ingredient(ingredient)
                        .quantity(rs.getBigDecimal("quantity"))
                        .unit(rs.getString("unit"))
                        .notes(rs.getString("notes"))
                        .build());
                }
            }
        }
        return ingredients;
    }
    
    private Nutrition loadNutrition(Connection conn, int recipeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_NUTRITION_SQL)) {
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Nutrition.builder()
                        .recipeId(recipeId)
                        .calories(rs.getInt("calories"))
                        .protein(rs.getBigDecimal("protein"))
                        .carbs(rs.getBigDecimal("carbs"))
                        .fat(rs.getBigDecimal("fat"))
                        .fiber(rs.getBigDecimal("fiber"))
                        .sugar(rs.getBigDecimal("sugar"))
                        .sodium(rs.getBigDecimal("sodium"))
                        .build();
                }
            }
        }
        return Nutrition.EMPTY;
    }
    
    private void saveIngredients(Connection conn, int recipeId, List<RecipeIngredient> ingredients) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_RECIPE_INGREDIENT_SQL)) {
            for (RecipeIngredient ri : ingredients) {
                pstmt.setInt(1, recipeId);
                pstmt.setInt(2, ri.ingredient().id());
                pstmt.setBigDecimal(3, ri.quantity());
                pstmt.setString(4, ri.unit());
                pstmt.setString(5, ri.notes());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private void deleteIngredients(Connection conn, int recipeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_RECIPE_INGREDIENTS_SQL)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }
    
    private void saveNutrition(Connection conn, int recipeId, Nutrition nutrition) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_NUTRITION_SQL)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, nutrition.calories());
            pstmt.setBigDecimal(3, nutrition.protein());
            pstmt.setBigDecimal(4, nutrition.carbs());
            pstmt.setBigDecimal(5, nutrition.fat());
            pstmt.setBigDecimal(6, nutrition.fiber());
            pstmt.setBigDecimal(7, nutrition.sugar());
            pstmt.setBigDecimal(8, nutrition.sodium());
            pstmt.executeUpdate();
        }
    }
    
    private Integer getInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
    
    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}