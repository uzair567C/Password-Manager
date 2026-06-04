package com.recipe.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.Ingredient;

public class IngredientDAO {
    
    private final DatabaseConnection dbConnection;
    
    private static final String SELECT_ALL_SQL = "SELECT * FROM ingredients ORDER BY name";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM ingredients WHERE id = ?";
    private static final String SELECT_BY_NAME_SQL = "SELECT * FROM ingredients WHERE LOWER(name) = LOWER(?)";
    private static final String INSERT_SQL = """
        INSERT INTO ingredients (name, unit, unit_price, calories_per_unit, 
                                 protein_per_unit, carbs_per_unit, fat_per_unit)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;
    private static final String UPDATE_SQL = """
        UPDATE ingredients SET name = ?, unit = ?, unit_price = ?, calories_per_unit = ?,
                               protein_per_unit = ?, carbs_per_unit = ?, fat_per_unit = ?
        WHERE id = ?
        """;
    private static final String DELETE_SQL = "DELETE FROM ingredients WHERE id = ?";
    private static final String SEARCH_SQL = "SELECT * FROM ingredients WHERE name ILIKE ? LIMIT 20";
    
    public IngredientDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Ingredient> getAllIngredients() throws DatabaseException {
        List<Ingredient> ingredients = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            
            while (rs.next()) {
                ingredients.add(mapResultSetToIngredient(rs));
            }
            return ingredients;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting ingredients: " + e.getMessage(), e);
        }
    }
    
    public Optional<Ingredient> getIngredientById(int id) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToIngredient(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting ingredient: " + e.getMessage(), e);
        }
    }
    
    public Optional<Ingredient> findByName(String name) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_NAME_SQL)) {
            
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToIngredient(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error finding ingredient by name: " + e.getMessage(), e);
        }
    }
    
    public List<Ingredient> searchIngredients(String searchTerm) throws DatabaseException {
        List<Ingredient> ingredients = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_SQL)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(mapResultSetToIngredient(rs));
                }
            }
            return ingredients;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error searching ingredients: " + e.getMessage(), e);
        }
    }
    
    public Ingredient insertIngredient(Ingredient ingredient) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            
            pstmt.setString(1, ingredient.name());
            pstmt.setString(2, ingredient.unit());
            pstmt.setBigDecimal(3, ingredient.unitPrice() != null ? ingredient.unitPrice() : BigDecimal.ZERO);
            pstmt.setBigDecimal(4, ingredient.caloriesPerUnit() != null ? ingredient.caloriesPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(5, ingredient.proteinPerUnit() != null ? ingredient.proteinPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(6, ingredient.carbsPerUnit() != null ? ingredient.carbsPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(7, ingredient.fatPerUnit() != null ? ingredient.fatPerUnit() : BigDecimal.ZERO);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return Ingredient.builder()
                        .id(id)
                        .name(ingredient.name())
                        .unit(ingredient.unit())
                        .unitPrice(ingredient.unitPrice())
                        .caloriesPerUnit(ingredient.caloriesPerUnit())
                        .proteinPerUnit(ingredient.proteinPerUnit())
                        .carbsPerUnit(ingredient.carbsPerUnit())
                        .fatPerUnit(ingredient.fatPerUnit())
                        .build();
                }
            }
            throw new DatabaseException("Failed to insert ingredient");
            
        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                throw new DatabaseException("Ingredient already exists: " + ingredient.name());
            }
            throw new DatabaseException("Error inserting ingredient: " + e.getMessage(), e);
        }
    }
    
    public boolean updateIngredient(Ingredient ingredient) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            
            pstmt.setString(1, ingredient.name());
            pstmt.setString(2, ingredient.unit());
            pstmt.setBigDecimal(3, ingredient.unitPrice() != null ? ingredient.unitPrice() : BigDecimal.ZERO);
            pstmt.setBigDecimal(4, ingredient.caloriesPerUnit() != null ? ingredient.caloriesPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(5, ingredient.proteinPerUnit() != null ? ingredient.proteinPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(6, ingredient.carbsPerUnit() != null ? ingredient.carbsPerUnit() : BigDecimal.ZERO);
            pstmt.setBigDecimal(7, ingredient.fatPerUnit() != null ? ingredient.fatPerUnit() : BigDecimal.ZERO);
            pstmt.setInt(8, ingredient.id());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating ingredient: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteIngredient(int id) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting ingredient: " + e.getMessage(), e);
        }
    }
    
    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        return Ingredient.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .unit(rs.getString("unit"))
            .unitPrice(rs.getBigDecimal("unit_price") != null ? rs.getBigDecimal("unit_price") : BigDecimal.ZERO)
            .caloriesPerUnit(rs.getBigDecimal("calories_per_unit") != null ? rs.getBigDecimal("calories_per_unit") : BigDecimal.ZERO)
            .proteinPerUnit(rs.getBigDecimal("protein_per_unit") != null ? rs.getBigDecimal("protein_per_unit") : BigDecimal.ZERO)
            .carbsPerUnit(rs.getBigDecimal("carbs_per_unit") != null ? rs.getBigDecimal("carbs_per_unit") : BigDecimal.ZERO)
            .fatPerUnit(rs.getBigDecimal("fat_per_unit") != null ? rs.getBigDecimal("fat_per_unit") : BigDecimal.ZERO)
            .build();
    }
}