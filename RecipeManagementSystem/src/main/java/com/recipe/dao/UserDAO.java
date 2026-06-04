package com.recipe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.recipe.database.DatabaseConnection;
import com.recipe.exceptions.DatabaseException;
import com.recipe.models.User;

public class UserDAO {
    
    private final DatabaseConnection dbConnection;
    
    // SQL Queries using Text Blocks
    private static final String INSERT_USER_SQL = """
        INSERT INTO users (username, email, password_hash, full_name, calorie_goal) 
        VALUES (?, ?, ?, ?, ?) RETURNING id, created_at
        """;
    
    private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_BY_EMAIL_SQL = "SELECT * FROM users WHERE email = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_PROFILE_SQL = "UPDATE users SET email = ?, full_name = ?, calorie_goal = ? WHERE id = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE users SET password_hash = ? WHERE id = ?";
    private static final String UPDATE_LAST_LOGIN_SQL = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
    private static final String UPDATE_STATS_SQL = "UPDATE users SET total_recipes = total_recipes + ?, total_cook_count = total_cook_count + ? WHERE id = ?";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users ORDER BY username";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";
    private static final String EXISTS_USERNAME_SQL = "SELECT 1 FROM users WHERE username = ?";
    private static final String EXISTS_EMAIL_SQL = "SELECT 1 FROM users WHERE email = ?";
    private static final String GET_THEME_SQL = "SELECT theme_preference FROM users WHERE id = ?";
    private static final String UPDATE_THEME_SQL = "UPDATE users SET theme_preference = ? WHERE id = ?";
    private static final String UPDATE_CALORIE_GOAL_SQL = "UPDATE users SET calorie_goal = ? WHERE id = ?";
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public User insertUser(User user) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_SQL)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getFullName());
            pstmt.setInt(5, user.getCalorieGoal());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getInt("id"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    return user;
                }
            }
            throw new DatabaseException("Failed to insert user - no ID returned");
            
        } catch (SQLException e) {
            throw mapSQLException(e);
        }
    }
    
    public User getUserByUsername(String username) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user: " + e.getMessage(), e);
        }
    }
    
    public User getUserByEmail(String email) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user: " + e.getMessage(), e);
        }
    }
    
    public User getUserById(int id) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting user: " + e.getMessage(), e);
        }
    }
    
    public boolean updateProfile(int userId, String email, String fullName, Integer calorieGoal) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PROFILE_SQL)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, fullName);
            pstmt.setInt(3, calorieGoal != null ? calorieGoal : 2000);
            pstmt.setInt(4, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating profile: " + e.getMessage(), e);
        }
    }
    
    public boolean updatePassword(int userId, String newPasswordHash) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating password: " + e.getMessage(), e);
        }
    }
    
    public boolean updateLastLogin(int userId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_LAST_LOGIN_SQL)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating last login: " + e.getMessage(), e);
        }
    }
    
    public boolean updateStats(int userId, int recipeCountDelta, int cookCountDelta) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_STATS_SQL)) {
            
            pstmt.setInt(1, recipeCountDelta);
            pstmt.setInt(2, cookCountDelta);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating stats: " + e.getMessage(), e);
        }
    }
    
    public boolean updateCalorieGoal(int userId, int calorieGoal) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CALORIE_GOAL_SQL)) {
            
            pstmt.setInt(1, calorieGoal);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error updating calorie goal: " + e.getMessage(), e);
        }
    }
    
    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS_SQL)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all users: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteUser(int userId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER_SQL)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user: " + e.getMessage(), e);
        }
    }
    
    public boolean usernameExists(String username) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(EXISTS_USERNAME_SQL)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking username: " + e.getMessage(), e);
        }
    }
    
    public boolean emailExists(String email) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(EXISTS_EMAIL_SQL)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email: " + e.getMessage(), e);
        }
    }
    
    public String getThemePreference(int userId) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_THEME_SQL)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("theme_preference");
                }
                return "light";
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Error getting theme preference: " + e.getMessage(), e);
        }
    }
    
    public void saveThemePreference(int userId, String theme) throws DatabaseException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_THEME_SQL)) {
            
            pstmt.setString(1, theme);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Error saving theme preference: " + e.getMessage(), e);
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setCalorieGoal(rs.getInt("calorie_goal"));
        user.setTotalRecipes(rs.getInt("total_recipes"));
        user.setTotalCookCount(rs.getInt("total_cook_count"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }
    
    private DatabaseException mapSQLException(SQLException e) {
        String message = e.getMessage();
        if (message != null && message.contains("username") && message.contains("unique")) {
            return new DatabaseException("Username already exists");
        }
        if (message != null && message.contains("email") && message.contains("unique")) {
            return new DatabaseException("Email already exists");
        }
        return new DatabaseException("Error inserting user: " + e.getMessage(), e);
    }
}