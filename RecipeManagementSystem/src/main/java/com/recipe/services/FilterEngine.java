package com.recipe.services;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.Recipe;
import com.recipe.models.SearchFilter;

public class FilterEngine {
    
    private final DatabaseConnection dbConnection;
    
    public FilterEngine() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Recipe> searchWithFilters(SearchFilter filter) throws DatabaseException {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
            FROM recipes r
            LEFT JOIN ratings rat ON r.id = rat.recipe_id
            LEFT JOIN nutrition n ON r.id = n.recipe_id
            LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id
            LEFT JOIN ingredients i ON ri.ingredient_id = i.id
            WHERE r.is_draft = false
        """);
        
        List<Object> params = new ArrayList<>();
        
        // Add filters
        if (filter.searchTerm() != null && !filter.searchTerm().isBlank()) {
            sql.append(" AND (r.title ILIKE ? OR r.description ILIKE ?)");
            params.add("%" + filter.searchTerm() + "%");
            params.add("%" + filter.searchTerm() + "%");
        }
        
        if (filter.minPrepTime() != null) {
            sql.append(" AND r.prep_time >= ?");
            params.add(filter.minPrepTime());
        }
        
        if (filter.maxPrepTime() != null) {
            sql.append(" AND r.prep_time <= ?");
            params.add(filter.maxPrepTime());
        }
        
        if (filter.minCookTime() != null) {
            sql.append(" AND r.cook_time >= ?");
            params.add(filter.minCookTime());
        }
        
        if (filter.maxCookTime() != null) {
            sql.append(" AND r.cook_time <= ?");
            params.add(filter.maxCookTime());
        }
        
        if (filter.minCalories() != null) {
            sql.append(" AND n.calories >= ?");
            params.add(filter.minCalories());
        }
        
        if (filter.maxCalories() != null) {
            sql.append(" AND n.calories <= ?");
            params.add(filter.maxCalories());
        }
        
        if (filter.minServings() != null) {
            sql.append(" AND r.servings >= ?");
            params.add(filter.minServings());
        }
        
        if (filter.maxServings() != null) {
            sql.append(" AND r.servings <= ?");
            params.add(filter.maxServings());
        }
        
        if (filter.minRating() != null) {
            sql.append(" AND COALESCE(AVG(rat.stars), 0) >= ?");
            params.add(filter.minRating());
        }
        
        if (filter.difficulty() != null && !filter.difficulty().isEmpty()) {
            sql.append(" AND r.difficulty = ?");
            params.add(filter.difficulty());
        }
        
        if (filter.maxCost() != null) {
            sql.append(" AND r.cost <= ?");
            params.add(filter.maxCost());
        }
        
        sql.append(" GROUP BY r.id ORDER BY r.created_at DESC");
        
        return executeQuery(sql.toString(), params);
    }
    
    public List<Recipe> searchByIngredients(List<String> ingredients, boolean requireAll) throws DatabaseException {
        if (ingredients == null || ingredients.isEmpty()) {
            return new ArrayList<>();
        }
        
        StringBuilder sql = new StringBuilder("""
            SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating,
                   COUNT(DISTINCT i.id) as matching_ingredients
            FROM recipes r
            LEFT JOIN ratings rat ON r.id = rat.recipe_id
            LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id
            LEFT JOIN ingredients i ON ri.ingredient_id = i.id
            WHERE r.is_draft = false
              AND i.name ILIKE ANY(?)
            GROUP BY r.id
        """);
        
        if (requireAll) {
            sql.append(" HAVING COUNT(DISTINCT i.id) >= ?");
        }
        
        sql.append(" ORDER BY matching_ingredients DESC, r.created_at DESC");
        
        List<Object> params = new ArrayList<>();
        String[] patterns = ingredients.stream()
            .map(ing -> "%" + ing + "%")
            .toArray(String[]::new);
        params.add(patterns);
        
        if (requireAll) {
            params.add(ingredients.size());
        }
        
        return executeQuery(sql.toString(), params);
    }
    
    public List<Recipe> getFavouriteRecipes(int userId) throws DatabaseException {
        String sql = """
            SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
            FROM recipes r
            JOIN favourites f ON r.id = f.recipe_id
            LEFT JOIN ratings rat ON r.id = rat.recipe_id
            WHERE f.user_id = ? AND r.is_draft = false
            GROUP BY r.id
            ORDER BY f.created_at DESC
            """;
        
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        return executeQuery(sql, params);
    }
    
    public List<Recipe> getTopRatedRecipes(int limit) throws DatabaseException {
        String sql = """
            SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
            FROM recipes r
            LEFT JOIN ratings rat ON r.id = rat.recipe_id
            WHERE r.is_draft = false
            GROUP BY r.id
            HAVING COUNT(rat.id) > 0
            ORDER BY avg_rating DESC, COUNT(rat.id) DESC
            LIMIT ?
            """;
        
        List<Object> params = new ArrayList<>();
        params.add(limit);
        
        return executeQuery(sql, params);
    }
    
    public List<Recipe> getMostCookedRecipes(int limit) throws DatabaseException {
        String sql = """
            SELECT r.*, COALESCE(AVG(rat.stars), 0) as avg_rating
            FROM recipes r
            LEFT JOIN ratings rat ON r.id = rat.recipe_id
            WHERE r.is_draft = false AND r.cook_count > 0
            GROUP BY r.id
            ORDER BY r.cook_count DESC
            LIMIT ?
            """;
        
        List<Object> params = new ArrayList<>();
        params.add(limit);
        
        return executeQuery(sql, params);
    }
    
    private List<Recipe> executeQuery(String sql, List<Object> params) throws DatabaseException {
        List<Recipe> recipes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof String[]) {
                    Array array = conn.createArrayOf("text", (String[]) param);
                    pstmt.setArray(i + 1, array);
                } else {
                    pstmt.setObject(i + 1, param);
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
            }
            return recipes;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error executing search: " + e.getMessage(), e);
        }
    }
    
    private Recipe mapResultSetToRecipe(ResultSet rs) throws SQLException {
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
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
            .ingredients(new ArrayList<>())
            .nutrition(null)
            .build();
    }
    
    private Integer getInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}