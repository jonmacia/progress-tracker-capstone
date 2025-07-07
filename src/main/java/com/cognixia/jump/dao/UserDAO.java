package com.cognixia.jump.dao;

import com.cognixia.jump.model.User;
import com.cognixia.jump.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * USER DAO INTERFACE
 * 
 * Data Access Object pattern for User entities.
 * Defines all database operations for User objects.
 * 
 */
public interface UserDAO {
    
    /**
     * CREATE - Add a new user to the database
     * 
     * @param user the user to add (without ID)
     * @return the created user with generated ID
     * @throws Exception if user creation fails
     */
    User createUser(User user) throws Exception;
    
    /**
     * READ - Find user by ID
     * 
     * @param userId the ID to search for
     * @return Optional containing user if found, empty otherwise
     * @throws UserNotFoundException if user doesn't exist
     */
    Optional<User> findById(int userId) throws UserNotFoundException;
    
    /**
     * READ - Find user by username
     * 
     * @param username the username to search for
     * @return Optional containing user if found, empty otherwise
     * @throws UserNotFoundException if user doesn't exist
     */
    Optional<User> findByUsername(String username) throws UserNotFoundException;
    
    /**
     * READ - Get all users
     * 
     * @return List of all users in the system
     */
    List<User> getAllUsers();
    
    /**
     * UPDATE - Modify existing user
     * 
     * @param user the user with updated information
     * @return true if update successful, false otherwise
     * @throws UserNotFoundException if user doesn't exist
     */
    boolean updateUser(User user) throws UserNotFoundException;
    
    /**
     * UPDATE - Change user password
     * 
     * @param userId the user's ID
     * @param newPassword the new password
     * @return true if update successful, false otherwise
     * @throws UserNotFoundException if user doesn't exist
     */
    boolean updatePassword(int userId, String newPassword) throws UserNotFoundException;
    
    /**
     * DELETE - Remove user by ID
     * 
     * @param userId the ID of user to delete
     * @return true if deletion successful, false otherwise
     * @throws UserNotFoundException if user doesn't exist
     */
    boolean deleteUser(int userId) throws UserNotFoundException;
    
    /**
     * AUTHENTICATION - Verify username and password
     * 
     * @param username the username to check
     * @param password the password to verify
     * @return Optional containing user if credentials valid, empty otherwise
     */
    Optional<User> authenticateUser(String username, String password);
    
    /**
     * UTILITY - Check if username already exists
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean usernameExists(String username);
    
    /**
     * UTILITY - Get total user count
     * 
     * @return number of users in the system
     */
    int getUserCount();
}