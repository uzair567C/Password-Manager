package com.recipe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.Rating;

public class RatingDAO {
    
    private final DatabaseConnection dbConnection;
    
    private static final String INSERT_RATING_SQL = """
        INSERT INTO ratings (user_id, recipe_id, stars, review_text)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (user_id, recipe_id) DO UPDATE SET
            stars = EXCLUDED.stars,
            review_text = EXCLUDED.review_text,
            created_at = CURRENT_TIMESTAMP
        RETURNING id, created_at
        """;
    
    private static final String SELECT_BY_RECIPE_SQL = """
        SELECT r.*, u.username
        FROM ratings r
        JOIN users u ON r.user_id = u.id
        WHERE r.recipe_id = ?
        ORDER BY r.created_at DESC
        """;
    
    private static final String SELECT_BY_USER_SQL = """
        SELECT r.*, u.username
        FROM ratings r
        JOIN users u ON r.user_id = u.id
        WHERE r.user_id = ?
        ORDER BY r.created_at DESC
        """;
    
    private static final String SELECT_AVERAGE_SQL = """
        SELECT COALESCE(AVG(stars), 0) as avg_rating, COUNT(*) as rating_count
        FROM ratings
        WHERE recipe_id = ?
        """;
    
    private static final String SELECT_USER_RATING_SQL = """
        SELECT * FROM ratings WHERE user_id = ? AND recipe_id = ?
        """;
    
    private static final String DELETE_RATING_SQL = "DELETE FROM ratings WHERE id = ?";
    private static final String DELETE_BY_RECIPE_SQL = "DELETE FROM ratings WHERE recipe_id = ?";
    
    public RatingDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Rating saveRating(Rating rating) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_RATING_SQL)) {
            
            pstmt.setInt(1, rating.userId());
            pstmt.setInt(2, rating.recipeId());
            pstmt.setInt(3, rating.stars());
            pstmt.setString(4, rating.reviewText());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    return new Rating(id, rating.userId(), rating.recipeId(), rating.stars(),
                        rating.reviewText(), createdAt, rating.username());
                }
            }
            throw new DatabaseException("Failed to save rating");
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving rating: " + e.getMessage(), e);
        }
    }
    
    public List<Rating> getRatingsByRecipe(int recipeId) throws DatabaseException {
        List<Rating> ratings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_RECIPE_SQL)) {
            
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
            return ratings;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting ratings: " + e.getMessage(), e);
        }
    }
    
    public List<Rating> getRatingsByUser(int userId) throws DatabaseException {
        List<Rating> ratings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_USER_SQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
            return ratings;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user ratings: " + e.getMessage(), e);
        }
    }
    
    public Optional<Rating> getUserRatingForRecipe(int userId, int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_RATING_SQL)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user rating: " + e.getMessage(), e);
        }
    }
    
    public double getAverageRating(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_AVERAGE_SQL)) {
            
            pstmt.setInt(1, recipeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
                return 0.0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting average rating: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteRating(int ratingId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_RATING_SQL)) {
            
            pstmt.setInt(1, ratingId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting rating: " + e.getMessage(), e);
        }
    }
    
    public void deleteRatingsByRecipe(int recipeId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_BY_RECIPE_SQL)) {
            
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting recipe ratings: " + e.getMessage(), e);
        }
    }
    
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        return Rating.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .recipeId(rs.getInt("recipe_id"))
            .stars(rs.getInt("stars"))
            .reviewText(rs.getString("review_text"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .username(rs.getString("username"))
            .build();
    }
}