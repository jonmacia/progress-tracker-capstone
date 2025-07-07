package com.cognixia.jump.dao;

import com.cognixia.jump.model.User;
import com.cognixia.jump.exception.UserNotFoundException;
import com.cognixia.jump.connection.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * USER DAO IMPLEMENTATION
 * 
 * Concrete implementation of UserDAO using MySQL database.
 * Handles all CRUD operations for User entities using JDBC.
 */
public class UserDAOImpl implements UserDAO {
    
    private final ConnectionManager connectionManager;
    
    public UserDAOImpl() {
        this.connectionManager = ConnectionManager.getInstance();
    }
    
    /**
     * CREATE USER
     */
    @Override
    public User createUser(User user) throws Exception {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            // Get the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            throw new Exception("Failed to create user: " + e.getMessage(), e);
        }
    }
    
    /**
     * FIND BY ID
     */
    @Override
    public Optional<User> findById(int userId) throws UserNotFoundException {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        
        throw new UserNotFoundException(userId);
    }
    
    /**
     * FIND BY USERNAME
     */
    @Override
    public Optional<User> findByUsername(String username) throws UserNotFoundException {
        String sql = "SELECT * FROM user WHERE username = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        
        throw new UserNotFoundException(username);
    }
    
    /**
     * GET ALL USERS
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY username";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * UPDATE USER
     */
    @Override
    public boolean updateUser(User user) throws UserNotFoundException {
        String sql = "UPDATE user SET username = ?, email = ? WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new UserNotFoundException(user.getUserId());
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UPDATE PASSWORD
     */
    @Override
    public boolean updatePassword(int userId, String newPassword) throws UserNotFoundException {
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new UserNotFoundException(userId);
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * DELETE USER
     */
    @Override
    public boolean deleteUser(int userId) throws UserNotFoundException {
        String sql = "DELETE FROM user WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new UserNotFoundException(userId);
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * AUTHENTICATE USER
     */
    @Override
    public Optional<User> authenticateUser(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * CHECK USERNAME EXISTS
     */
    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * GET USER COUNT
     */
    @Override
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM user";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * HELPER METHOD - Extract User from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            user.setCreatedDate(createdTimestamp.toLocalDateTime());
        }
        
        return user;
    }
}