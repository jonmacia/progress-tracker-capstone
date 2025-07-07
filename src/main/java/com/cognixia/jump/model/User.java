package com.cognixia.jump.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * USER MODEL CLASS
 * 
 * This class represents a User in the Progress Tracker system.
 * It maps directly to the 'user' table in the database.
 * 
 * Design principles:
 * - Private fields with public getters/setters
 * - Validation in setters to ensure data integrity
 * - Multiple constructors for flexibility
 * - Override toString() for debugging
 */
public class User {
    
    // Fields matching database columns
    private int userId;              // Primary key, auto-generated
    private String username;         // Unique username for login
    private String password;         // Password
    private String email;            // Optional email address
    private LocalDateTime createdDate; // When account was created
    
    // Default constructor - used when creating new users
    public User() {
        this.createdDate = LocalDateTime.now();
    }
    
    // Constructor for registration (no ID yet)
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdDate = LocalDateTime.now();
    }
    
    // Full constructor - used when loading from database
    public User(int userId, String username, String password, String email, LocalDateTime createdDate) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdDate = createdDate;
    }
    
    // Getters
    public int getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    // Setters with validation
    public void setUserId(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User ID cannot be negative");
        }
        this.userId = userId;
    }
    
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }
        this.username = username.trim();
    }
    
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() > 255) {
            throw new IllegalArgumentException("Password cannot exceed 255 characters");
        }
        this.password = password;
    }
    
    public void setEmail(String email) {
        if (email != null && email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
        // Basic email validation
        if (email != null && !email.trim().isEmpty() && !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // Utility methods
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("User{id=%d, username='%s', email='%s', created=%s}", 
                userId, username, email, 
                createdDate != null ? createdDate.format(formatter) : "null");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
    
    // Helper method to display user info without password
    public String getDisplayInfo() {
        return String.format("Username: %s | Email: %s | Member since: %s",
                username, 
                email != null ? email : "Not provided",
                createdDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
    }
}