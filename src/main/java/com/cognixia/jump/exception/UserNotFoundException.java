package com.cognixia.jump.exception;

/**
 * USER NOT FOUND EXCEPTION
 * 
 * Thrown when attempting to retrieve a user that doesn't exist in the database.
 * This is a checked exception that must be handled or declared.
 */
public class UserNotFoundException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    // Constructor with user ID
    public UserNotFoundException(int userId) {
        super("User with ID " + userId + " was not found in the database");
    }
    
    // Constructor with username
    public UserNotFoundException(String username) {
        super("User with username '" + username + "' was not found in the database");
    }
    
    // Constructor with custom message
    public UserNotFoundException(String message, boolean custom) {
        super(message);
    }
    
    // Constructor with message and cause
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}