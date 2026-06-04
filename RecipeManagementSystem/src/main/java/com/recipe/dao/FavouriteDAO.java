package com.recipe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;

public class FavouriteDAO {
    
    private final DatabaseConnection dbConnection;
    
    private static final String ADD_FAVOURITE_SQL = """
        INSERT INTO favourites (user_id, recipe_id)
        VALUES (?, ?)
        ON CONFLICT (user_id, recipe_id) DO NOTHING
        """;
    
    private static final String REMOVE_FAVOURITE_SQL = "DELETE FROM favourites WHERE user_id = ? AND recipe_id = ?";
    private static final String IS_FAVOURITE_SQL = "SELECT 1 FROM favourites WHERE user_id = ? AND recipe_id = ?";
    private static final String GET_USER_FAVOURITES_SQL = """
        SELECT recipe_id FROM favourites WHERE user_id = ?
        """;
    private static final String GET_FAVOURITE_COUNT_SQL = "SELECT COUNT(*) FROM favourites WHERE recipe_id = ?";
    
    public FavouriteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public boolean addFavourite(int userId, int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADD_FAVOURITE_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error adding favourite: " + e.getMessage(), e);
        }
    }
    
    public boolean removeFavourite(int userId, int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(REMOVE_FAVOURITE_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error removing favourite: " + e.getMessage(), e);
        }
    }
    
    public boolean isFavourite(int userId, int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(IS_FAVOURITE_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking favourite: " + e.getMessage(), e);
        }
    }
    
    public List<Integer> getUserFavourites(int userId) throws DatabaseException {
        List<Integer> favouriteIds = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_USER_FAVOURITES_SQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    favouriteIds.add(rs.getInt("recipe_id"));
                }
            }
            return favouriteIds;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting favourites: " + e.getMessage(), e);
        }
    }
    
    public int getFavouriteCount(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_FAVOURITE_COUNT_SQL)) {
            
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting favourite count: " + e.getMessage(), e);
        }
    }
}