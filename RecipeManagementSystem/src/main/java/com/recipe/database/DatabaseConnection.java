package com.recipe.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "recipe_management";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres"; // CHANGE THIS TO YOUR PASSWORD
    
    private DatabaseConnection() {
        try {
            System.out.println("Initializing database connection...");
            initializeConnectionPool();
            System.out.println("Database connection pool initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    private void initializeConnectionPool() {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        
        System.out.println("JDBC URL: " + jdbcUrl);
        System.out.println("Username: " + DB_USER);
        
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        try {
            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP DataSource created successfully!");
        } catch (Exception e) {
            System.err.println("Failed to create HikariCP DataSource: " + e.getMessage());
            throw e;
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            System.out.println("DataSource is null or closed, reinitializing...");
            initializeConnectionPool();
        }
        Connection conn = dataSource.getConnection();
        System.out.println("Connection obtained successfully!");
        return conn;
    }
    
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Connection pool closed.");
        }
    }
    
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            System.out.println("Test connection result: " + isValid);
            return isValid;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}