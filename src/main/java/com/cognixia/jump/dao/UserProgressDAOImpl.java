package com.cognixia.jump.dao;

import com.cognixia.jump.model.UserProgress;
import com.cognixia.jump.model.User;
import com.cognixia.jump.model.Topic;
import com.cognixia.jump.exception.ProgressAlreadyExistsException;
import com.cognixia.jump.exception.UserNotFoundException;
import com.cognixia.jump.connection.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * USER PROGRESS DAO IMPLEMENTATION
 * 
 * Handles all database operations for user progress tracking.
 */
public class UserProgressDAOImpl implements UserProgressDAO {
    
    private final ConnectionManager connectionManager;
    private final UserDAO userDAO;
    private final TopicDAO topicDAO;
    
    public UserProgressDAOImpl() {
        this.connectionManager = ConnectionManager.getInstance();
        this.userDAO = new UserDAOImpl();
        this.topicDAO = new TopicDAOImpl();
    }
    
    /**
     * CREATE PROGRESS
     */
    @Override
    public UserProgress createProgress(UserProgress progress) throws ProgressAlreadyExistsException, Exception {
        // Check if progress already exists
        if (isUserTrackingTopic(progress.getUserId(), progress.getTopicId())) {
            throw new ProgressAlreadyExistsException(progress.getUserId(), progress.getTopicId());
        }
        
        String sql = "INSERT INTO user_progress (user_id, topic_id, status, current_progress, " +
                     "rating, notes, start_date, completion_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, progress.getUserId());
            pstmt.setInt(2, progress.getTopicId());
            pstmt.setString(3, progress.getStatus().name());
            pstmt.setInt(4, progress.getCurrentProgress());
            pstmt.setObject(5, progress.getRating());
            pstmt.setString(6, progress.getNotes());
            pstmt.setObject(7, progress.getStartDate());
            pstmt.setObject(8, progress.getCompletionDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating progress failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    progress.setProgressId(generatedKeys.getInt(1));
                    return progress;
                } else {
                    throw new SQLException("Creating progress failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating progress: " + e.getMessage());
            throw new Exception("Failed to create progress: " + e.getMessage(), e);
        }
    }
    
    /**
     * FIND BY ID
     */
    @Override
    public Optional<UserProgress> findById(int progressId) {
        String sql = "SELECT * FROM user_progress WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, progressId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                UserProgress progress = extractProgressFromResultSet(rs);
                // Load related entities
                loadRelatedEntities(progress);
                return Optional.of(progress);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding progress by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * FIND BY USER AND TOPIC
     */
    @Override
    public Optional<UserProgress> findByUserAndTopic(int userId, int topicId) {
        String sql = "SELECT * FROM user_progress WHERE user_id = ? AND topic_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, topicId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                UserProgress progress = extractProgressFromResultSet(rs);
                loadRelatedEntities(progress);
                return Optional.of(progress);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding progress by user and topic: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * GET USER PROGRESS
     */
    @Override
    public List<UserProgress> getUserProgress(int userId) throws UserNotFoundException {
        // Verify user exists
        Optional<User> user = userDAO.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        
        List<UserProgress> progressList = new ArrayList<>();
        String sql = "SELECT up.*, t.title, t.director, t.release_year " +
                     "FROM user_progress up " +
                     "JOIN topic t ON up.topic_id = t.topic_id " +
                     "WHERE up.user_id = ? " +
                     "ORDER BY up.last_updated DESC";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UserProgress progress = extractProgressFromResultSet(rs);
                loadRelatedEntities(progress);
                progressList.add(progress);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user progress: " + e.getMessage());
        }
        
        return progressList;
    }
    
    /**
     * GET USER PROGRESS BY STATUS
     */
    @Override
    public List<UserProgress> getUserProgressByStatus(int userId, UserProgress.Status status) throws UserNotFoundException {
        // Verify user exists
        Optional<User> user = userDAO.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        
        List<UserProgress> progressList = new ArrayList<>();
        String sql = "SELECT up.*, t.title, t.director, t.release_year " +
                     "FROM user_progress up " +
                     "JOIN topic t ON up.topic_id = t.topic_id " +
                     "WHERE up.user_id = ? AND up.status = ? " +
                     "ORDER BY up.last_updated DESC";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, status.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UserProgress progress = extractProgressFromResultSet(rs);
                loadRelatedEntities(progress);
                progressList.add(progress);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user progress by status: " + e.getMessage());
        }
        
        return progressList;
    }
    
    /**
     * UPDATE PROGRESS
     */
    @Override
    public boolean updateProgress(UserProgress progress) {
        String sql = "UPDATE user_progress " +
                     "SET status = ?, current_progress = ?, rating = ?, notes = ?, " +
                     "start_date = ?, completion_date = ? " +
                     "WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, progress.getStatus().name());
            pstmt.setInt(2, progress.getCurrentProgress());
            pstmt.setObject(3, progress.getRating());
            pstmt.setString(4, progress.getNotes());
            pstmt.setObject(5, progress.getStartDate());
            pstmt.setObject(6, progress.getCompletionDate());
            pstmt.setInt(7, progress.getProgressId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating progress: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UPDATE PROGRESS PERCENTAGE
     */
    @Override
    public boolean updateProgressPercentage(int progressId, int percentage) {
        String sql = "UPDATE user_progress " +
                     "SET current_progress = ?, " +
                     "status = CASE " +
                     "    WHEN ? = 0 THEN 'PLAN_TO_START' " +
                     "    WHEN ? = 100 THEN 'COMPLETED' " +
                     "    ELSE 'IN_PROGRESS' " +
                     "END, " +
                     "start_date = CASE " +
                     "    WHEN ? > 0 AND start_date IS NULL THEN CURRENT_DATE " +
                     "    ELSE start_date " +
                     "END, " +
                     "completion_date = CASE " +
                     "    WHEN ? = 100 THEN CURRENT_DATE " +
                     "    ELSE NULL " +
                     "END " +
                     "WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, percentage);
            pstmt.setInt(2, percentage);
            pstmt.setInt(3, percentage);
            pstmt.setInt(4, percentage);
            pstmt.setInt(5, percentage);
            pstmt.setInt(6, progressId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating progress percentage: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UPDATE RATING
     */
    @Override
    public boolean updateRating(int progressId, double rating) {
        String sql = "UPDATE user_progress SET rating = ? WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, rating);
            pstmt.setInt(2, progressId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating rating: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UPDATE NOTES
     */
    @Override
    public boolean updateNotes(int progressId, String notes) {
        String sql = "UPDATE user_progress SET notes = ? WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, notes);
            pstmt.setInt(2, progressId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating notes: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * DELETE PROGRESS
     */
    @Override
    public boolean deleteProgress(int progressId) {
        String sql = "DELETE FROM user_progress WHERE progress_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, progressId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting progress: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * DELETE USER PROGRESS
     */
    @Override
    public boolean deleteUserProgress(int userId) throws UserNotFoundException {
        // Verify user exists
        Optional<User> user = userDAO.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        
        String sql = "DELETE FROM user_progress WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user progress: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * IS USER TRACKING TOPIC
     */
    @Override
    public boolean isUserTrackingTopic(int userId, int topicId) {
        String sql = "SELECT COUNT(*) FROM user_progress WHERE user_id = ? AND topic_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, topicId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if user is tracking topic: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * GET USER PROGRESS SUMMARY
     */
    @Override
    public UserProgressSummary getUserProgressSummary(int userId) throws UserNotFoundException {
        // Verify user exists
        Optional<User> user = userDAO.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN status = 'PLAN_TO_START' THEN 1 ELSE 0 END) as plan_to_start, " +
                     "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress, " +
                     "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed " +
                     "FROM user_progress WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new UserProgressSummary(
                    rs.getInt("total"),
                    rs.getInt("plan_to_start"),
                    rs.getInt("in_progress"),
                    rs.getInt("completed")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user progress summary: " + e.getMessage());
        }
        
        // Return empty summary if no data found
        return new UserProgressSummary(0, 0, 0, 0);
    }
    
    /**
     * GET TOPIC STATISTICS
     */
    @Override
    public TopicStats getTopicStatistics(int topicId) {
        String sql = "SELECT " +
                     "COUNT(*) as total_users, " +
                     "SUM(CASE WHEN status = 'PLAN_TO_START' THEN 1 ELSE 0 END) as plan_to_start, " +
                     "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress, " +
                     "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
                     "AVG(rating) as avg_rating " +
                     "FROM user_progress WHERE topic_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, topicId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new TopicStats(
                    rs.getInt("total_users"),
                    rs.getInt("plan_to_start"),
                    rs.getInt("in_progress"),
                    rs.getInt("completed"),
                    rs.getDouble("avg_rating")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting topic statistics: " + e.getMessage());
        }
        
        // Return empty stats if no data found
        return new TopicStats(0, 0, 0, 0, 0.0);
    }
    
    /**
     * HELPER METHOD - Extract UserProgress from ResultSet
     */
    private UserProgress extractProgressFromResultSet(ResultSet rs) throws SQLException {
        UserProgress progress = new UserProgress();
        progress.setProgressId(rs.getInt("progress_id"));
        progress.setUserId(rs.getInt("user_id"));
        progress.setTopicId(rs.getInt("topic_id"));
        progress.setStatus(UserProgress.Status.valueOf(rs.getString("status")));
        progress.setCurrentProgress(rs.getInt("current_progress"));
        
        // Handle nullable fields
        Double rating = rs.getDouble("rating");
        if (!rs.wasNull()) {
            progress.setRating(rating);
        }
        
        progress.setNotes(rs.getString("notes"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            progress.setStartDate(startDate.toLocalDate());
        }
        
        Date completionDate = rs.getDate("completion_date");
        if (completionDate != null) {
            progress.setCompletionDate(completionDate.toLocalDate());
        }
        
        Timestamp lastUpdated = rs.getTimestamp("last_updated");
        if (lastUpdated != null) {
            progress.setLastUpdated(lastUpdated.toLocalDateTime());
        }
        
        return progress;
    }
    
    /**
     * HELPER METHOD - Load related User and Topic entities
     */
    private void loadRelatedEntities(UserProgress progress) {
        try {
            // Load User
            Optional<User> user = userDAO.findById(progress.getUserId());
            user.ifPresent(progress::setUser);
            
            // Load Topic
            Optional<Topic> topic = topicDAO.findById(progress.getTopicId());
            topic.ifPresent(progress::setTopic);
            
        } catch (Exception e) {
            System.err.println("Error loading related entities: " + e.getMessage());
        }
    }
}