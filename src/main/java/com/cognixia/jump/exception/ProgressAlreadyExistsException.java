package com.cognixia.jump.exception;

/**
 * PROGRESS ALREADY EXISTS EXCEPTION
 * 
 * Thrown when attempting to create a progress entry that already exists.
 * A user can only have one progress entry per topic.
 */
public class ProgressAlreadyExistsException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private int topicId;
    
    // Constructor with user and topic IDs
    public ProgressAlreadyExistsException(int userId, int topicId) {
        super(String.format("Progress entry already exists for user %d and topic %d", userId, topicId));
        this.userId = userId;
        this.topicId = topicId;
    }
    
    // Constructor with user ID, topic ID, and topic title
    public ProgressAlreadyExistsException(int userId, int topicId, String topicTitle) {
        super(String.format("User %d is already tracking '%s' (topic %d)", userId, topicTitle, topicId));
        this.userId = userId;
        this.topicId = topicId;
    }
    
    // Getters for additional context
    public int getUserId() {
        return userId;
    }
    
    public int getTopicId() {
        return topicId;
    }
}