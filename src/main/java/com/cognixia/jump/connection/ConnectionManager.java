package com.cognixia.jump.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CONNECTION MANAGER
 * 
 * Manages database connections for the Progress Tracker application.
 * Implements the Singleton pattern to ensure centralized connection management.
 * 
 */
public class ConnectionManager {
    
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/progress_tracker_db?serverTimezone=UTC";
    private static final String USERNAME = "root";  // Change
    private static final String PASSWORD = "yourpassword";  // Change
    
    // JDBC driver class name for MySQL 8.0
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Singleton instance
    private static ConnectionManager instance = null;
    
    // Private constructor prevents external instantiation
    private ConnectionManager() {
        try {
            // Load the MySQL JDBC driver
            Class.forName(DRIVER);
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error loading MySQL JDBC Driver: " + e.getMessage());
            throw new RuntimeException("Failed to load database driver", e);
        }
    }
    
    /**
     * Gets the singleton instance of ConnectionManager
     * Thread-safe implementation using synchronized keyword
     * 
     * @return the single ConnectionManager instance
     */
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }
    
    /**
     * Creates and returns a new database connection
     * Each DAO method should get its own connection and close it when done
     * 
     * @return Connection object to the database
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
            // Set connection properties
            connection.setAutoCommit(true);  // Auto-commit for simplicity
            
            return connection;
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to establish database connection: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'progress_tracker_db' exists");
            System.err.println("3. Username and password are correct");
            System.err.println("4. MySQL is accessible on localhost:3306");
            throw e;
        }
    }
    
    /**
     * Test the database connection
     * Used by the main application to verify connectivity on startup
     * 
     * @return true if connection successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get connection URL for display/debugging
     * Masks the password for security
     * 
     * @return masked connection string
     */
    public String getConnectionInfo() {
        return String.format("URL: %s | User: %s | Password: %s", 
                URL, USERNAME, PASSWORD.isEmpty() ? "[none]" : "[hidden]");
    }
}