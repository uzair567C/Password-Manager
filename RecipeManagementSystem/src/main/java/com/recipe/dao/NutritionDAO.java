package com.recipe.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.Nutrition;

public class NutritionDAO {
    
    private final DatabaseConnection dbConnection;
    
    private static final String SELECT_BY_RECIPE_ID_SQL = "SELECT * FROM nutrition WHERE recipe_id = ?";
    private static final String INSERT_OR_UPDATE_SQL = """
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
    
    private static final String DELETE_SQL = "DELETE FROM nutrition WHERE recipe_id = ?";
    
    public NutritionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Optional<Nutrition> getNutritionByRecipeId(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_RECIPE_ID_SQL)) {
            
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNutrition(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting nutrition: " + e.getMessage(), e);
        }
    }
    
    public Nutrition saveNutrition(Nutrition nutrition) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_OR_UPDATE_SQL)) {
            
            pstmt.setInt(1, nutrition.recipeId());
            pstmt.setInt(2, nutrition.calories());
            pstmt.setBigDecimal(3, nutrition.protein());
            pstmt.setBigDecimal(4, nutrition.carbs());
            pstmt.setBigDecimal(5, nutrition.fat());
            pstmt.setBigDecimal(6, nutrition.fiber());
            pstmt.setBigDecimal(7, nutrition.sugar());
            pstmt.setBigDecimal(8, nutrition.sodium());
            
            pstmt.executeUpdate();
            return nutrition;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving nutrition: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteNutrition(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            
            pstmt.setInt(1, recipeId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting nutrition: " + e.getMessage(), e);
        }
    }
    
    private Nutrition mapResultSetToNutrition(ResultSet rs) throws SQLException {
        return Nutrition.builder()
            .recipeId(rs.getInt("recipe_id"))
            .calories(rs.getInt("calories"))
            .protein(rs.getBigDecimal("protein") != null ? rs.getBigDecimal("protein") : BigDecimal.ZERO)
            .carbs(rs.getBigDecimal("carbs") != null ? rs.getBigDecimal("carbs") : BigDecimal.ZERO)
            .fat(rs.getBigDecimal("fat") != null ? rs.getBigDecimal("fat") : BigDecimal.ZERO)
            .fiber(rs.getBigDecimal("fiber"))
            .sugar(rs.getBigDecimal("sugar"))
            .sodium(rs.getBigDecimal("sodium"))
            .build();
    }
}