package com.au.ahmad.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/* 
Database Connection
+------------------+----------------------------+------------------------------------------------------+
|function          | input                      | output                                               |
+------------------+----------------------------+------------------------------------------------------+
|DatabaseConnection| -                          | DatabaseConnection instance (singleton)              |
|getInstance       | -                          | DatabaseConnection instance (singleton)              |
|getConnection     | -                          | Connection object for executing SQL queries          |
|initializeTables  | -                          | void (creates necessary tables if they don't exist)  |
|close             | -                          | void (closes the database connection)                |
+------------------+----------------------------+------------------------------------------------------+

*/

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:password_manager.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            this.connection.setAutoCommit(true);
            initializeTables();
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id  INTEGER PRIMARY KEY AUTOINCREMENT,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS credentials (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id          INTEGER NOT NULL,
                    site             VARCHAR(200) NOT NULL,
                    account_username VARCHAR(200) NOT NULL,
                    password         VARCHAR(500) NOT NULL,
                    priority         VARCHAR(10)  NOT NULL DEFAULT 'Medium',
                    favourite        INTEGER      NOT NULL DEFAULT 0,
                    created_at       DATETIME     NOT NULL DEFAULT (datetime('now')),
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                        ON DELETE CASCADE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS login_history (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id     INTEGER  NOT NULL,
                    login_time  DATETIME NOT NULL DEFAULT (datetime('now')),
                    logout_time DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                        ON DELETE CASCADE
                )
            """);

            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_cred_user
                ON credentials(user_id)
            """);
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}