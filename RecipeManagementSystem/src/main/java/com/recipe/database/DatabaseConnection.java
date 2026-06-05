package com.recipe.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    
    private static final String DB_HOST;
    private static final String DB_PORT;
    private static final String DB_NAME;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    
    static {
        // Load from environment variables with sensible defaults
        DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
        DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
        DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "recipe_management";
        DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
        DB_PASSWORD = System.getenv("DB_PASSWORD");
        
        // Validate critical configuration
        if (DB_PASSWORD == null || DB_PASSWORD.isEmpty()) {
            throw new RuntimeException("CRITICAL: DB_PASSWORD environment variable not set. " +
                "Please set: export DB_PASSWORD=your_password");
        }
    }
    
    private DatabaseConnection() {
        try {
            initializeConnectionPool();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage(), e);
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HikariCP DataSource: " + e.getMessage(), e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializeConnectionPool();
        }
        return dataSource.getConnection();
    }
    
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}