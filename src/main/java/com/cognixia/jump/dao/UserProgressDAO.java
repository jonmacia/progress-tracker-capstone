package com.cognixia.jump.dao;

import com.cognixia.jump.model.UserProgress;
import com.cognixia.jump.exception.ProgressAlreadyExistsException;
import com.cognixia.jump.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * USER PROGRESS DAO INTERFACE
 * 
 * Data Access Object for UserProgress entities.
 * Manages the relationship between users and their film tracking progress.
 */
public interface UserProgressDAO {
    
    /**
     * CREATE - Add new progress tracking entry
     */
    UserProgress createProgress(UserProgress progress) throws ProgressAlreadyExistsException, Exception;
    
    /**
     * READ - Find progress by ID
     */
    Optional<UserProgress> findById(int progressId);
    
    /**
     * READ - Find progress by user and topic
     */
    Optional<UserProgress> findByUserAndTopic(int userId, int topicId);
    
    /**
     * READ - Get all progress entries for a user
     */
    List<UserProgress> getUserProgress(int userId) throws UserNotFoundException;
    
    /**
     * READ - Get progress by status for a user
     */
    List<UserProgress> getUserProgressByStatus(int userId, UserProgress.Status status) throws UserNotFoundException;
    
    /**
     * UPDATE - Update progress entry
     */
    boolean updateProgress(UserProgress progress);
    
    /**
     * UPDATE - Update only the progress percentage
     */
    boolean updateProgressPercentage(int progressId, int percentage);
    
    /**
     * UPDATE - Update rating
     */
    boolean updateRating(int progressId, double rating);
    
    /**
     * UPDATE - Update notes
     */
    boolean updateNotes(int progressId, String notes);
    
    /**
     * DELETE - Remove progress entry
     */
    boolean deleteProgress(int progressId);
    
    /**
     * DELETE - Remove all progress for a user
     */
    boolean deleteUserProgress(int userId) throws UserNotFoundException;
    
    /**
     * UTILITY - Check if user is tracking a topic
     */
    boolean isUserTrackingTopic(int userId, int topicId);
    
    /**
     * STATISTICS - Get user progress summary
     */
    UserProgressSummary getUserProgressSummary(int userId) throws UserNotFoundException;
    
    /**
     * STATISTICS - Get topic statistics
     */
    TopicStats getTopicStatistics(int topicId);
}



