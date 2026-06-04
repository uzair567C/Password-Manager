package com.recipe.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.MealPlan;

public class MealPlanDAO {
    
    private final DatabaseConnection dbConnection;
    
    private static final String INSERT_MEAL_PLAN_SQL = """
        INSERT INTO meal_plans (user_id, recipe_id, day_of_week, meal_type, week_start)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (user_id, day_of_week, meal_type, week_start) 
        DO UPDATE SET recipe_id = EXCLUDED.recipe_id
        RETURNING id
        """;
    
    private static final String DELETE_MEAL_PLAN_SQL = """
        DELETE FROM meal_plans 
        WHERE user_id = ? AND day_of_week = ? AND meal_type = ? AND week_start = ?
        """;
    
    private static final String SELECT_WEEK_PLANS_SQL = """
        SELECT mp.*, r.title as recipe_title, r.photo_path as recipe_photo_path
        FROM meal_plans mp
        JOIN recipes r ON mp.recipe_id = r.id
        WHERE mp.user_id = ? AND mp.week_start = ?
        ORDER BY mp.day_of_week, 
                 CASE mp.meal_type 
                     WHEN 'Breakfast' THEN 1 
                     WHEN 'Lunch' THEN 2 
                     WHEN 'Dinner' THEN 3 
                     WHEN 'Snack' THEN 4 
                 END
        """;
    
    private static final String DELETE_WEEK_PLANS_SQL = "DELETE FROM meal_plans WHERE user_id = ? AND week_start = ?";
    private static final String GET_PLANNED_RECIPES_FOR_WEEK_SQL = """
        SELECT DISTINCT mp.recipe_id, r.title, r.servings
        FROM meal_plans mp
        JOIN recipes r ON mp.recipe_id = r.id
        WHERE mp.user_id = ? AND mp.week_start = ?
        """;
    
    public MealPlanDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public MealPlan saveMealPlan(MealPlan mealPlan) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_MEAL_PLAN_SQL)) {
            
            pstmt.setInt(1, mealPlan.userId());
            pstmt.setInt(2, mealPlan.recipeId());
            pstmt.setInt(3, mealPlan.dayOfWeek());
            pstmt.setString(4, mealPlan.mealType());
            pstmt.setDate(5, Date.valueOf(mealPlan.weekStart()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return new MealPlan(id, mealPlan.userId(), mealPlan.recipeId(), 
                        mealPlan.dayOfWeek(), mealPlan.mealType(), mealPlan.weekStart(),
                        mealPlan.recipeTitle(), mealPlan.recipePhotoPath());
                }
            }
            throw new DatabaseException("Failed to save meal plan");
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving meal plan: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteMealPlan(int userId, int dayOfWeek, String mealType, LocalDate weekStart) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_MEAL_PLAN_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, dayOfWeek);
            pstmt.setString(3, mealType);
            pstmt.setDate(4, Date.valueOf(weekStart));
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting meal plan: " + e.getMessage(), e);
        }
    }
    
    public List<MealPlan> getWeekPlans(int userId, LocalDate weekStart) throws DatabaseException {
        List<MealPlan> plans = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_WEEK_PLANS_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(weekStart));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToMealPlan(rs));
                }
            }
            return plans;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting week plans: " + e.getMessage(), e);
        }
    }
    
    public void clearWeekPlans(int userId, LocalDate weekStart) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_WEEK_PLANS_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(weekStart));
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error clearing week plans: " + e.getMessage(), e);
        }
    }
    
    public List<PlannedRecipeInfo> getPlannedRecipesForWeek(int userId, LocalDate weekStart) throws DatabaseException {
        List<PlannedRecipeInfo> recipes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_PLANNED_RECIPES_FOR_WEEK_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(weekStart));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(new PlannedRecipeInfo(
                        rs.getInt("recipe_id"),
                        rs.getString("title"),
                        rs.getInt("servings")
                    ));
                }
            }
            return recipes;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting planned recipes: " + e.getMessage(), e);
        }
    }
    
    private MealPlan mapResultSetToMealPlan(ResultSet rs) throws SQLException {
        return MealPlan.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .recipeId(rs.getInt("recipe_id"))
            .dayOfWeek(rs.getInt("day_of_week"))
            .mealType(rs.getString("meal_type"))
            .weekStart(rs.getDate("week_start").toLocalDate())
            .recipeTitle(rs.getString("recipe_title"))
            .recipePhotoPath(rs.getString("recipe_photo_path"))
            .build();
    }
    
    public record PlannedRecipeInfo(int recipeId, String title, int servings) {}
}