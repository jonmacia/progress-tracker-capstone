package com.cognixia.jump.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * USER PROGRESS MODEL CLASS
 * 
 * This class represents a user's progress tracking for a specific film.
 * It maps to the 'user_progress' table and acts as the junction between users and topics.
 * 
 * For films: current_progress represents completion percentage (0-100)
 */
public class UserProgress {
    
    // Fields matching database columns
    private int progressId;             // Primary key
    private int userId;                // Foreign key to user
    private int topicId;               // Foreign key to topic (film)
    private Status status;             // Current tracking status
    private int currentProgress;       // For films: 0 (not started) or 100 (completed)
    private Double rating;             // User's rating (1.0-5.0)
    private String notes;              // Personal notes about the film
    private LocalDate startDate;       // When user started watching
    private LocalDate completionDate;  // When user finished watching
    private LocalDateTime lastUpdated; // Last modification timestamp
    
    // Linked objects (populated by DAO when needed)
    private User user;
    private Topic topic;
    
    // Enum for progress status
    public enum Status {
        PLAN_TO_START("Plan to Start"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        Status(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Default constructor
    public UserProgress() {
        this.status = Status.PLAN_TO_START;
        this.currentProgress = 0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Constructor for new progress entry
    public UserProgress(int userId, int topicId, Status status) {
        this();
        this.userId = userId;
        this.topicId = topicId;
        this.status = status;
        
        // Auto-set progress based on status
        if (status == Status.COMPLETED) {
            this.currentProgress = 100;
            this.completionDate = LocalDate.now();
        } else if (status == Status.IN_PROGRESS) {
            this.startDate = LocalDate.now();
        }
    }
    
    // Full constructor - used when loading from database
    public UserProgress(int progressId, int userId, int topicId, Status status,
                       int currentProgress, Double rating, String notes,
                       LocalDate startDate, LocalDate completionDate,
                       LocalDateTime lastUpdated) {
        this.progressId = progressId;
        this.userId = userId;
        this.topicId = topicId;
        this.status = status;
        this.currentProgress = currentProgress;
        this.rating = rating;
        this.notes = notes;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters
    public int getProgressId() {
        return progressId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public int getTopicId() {
        return topicId;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public int getCurrentProgress() {
        return currentProgress;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getCompletionDate() {
        return completionDate;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public User getUser() {
        return user;
    }
    
    public Topic getTopic() {
        return topic;
    }
    
    // Setters with validation and business logic
    public void setProgressId(int progressId) {
        if (progressId < 0) {
            throw new IllegalArgumentException("Progress ID cannot be negative");
        }
        this.progressId = progressId;
    }
    
    public void setUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.userId = userId;
    }
    
    public void setTopicId(int topicId) {
        if (topicId <= 0) {
            throw new IllegalArgumentException("Topic ID must be positive");
        }
        this.topicId = topicId;
    }
    
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
        
        // Auto-update dates based on status change
        if (status == Status.IN_PROGRESS && this.startDate == null) {
            this.startDate = LocalDate.now();
        } else if (status == Status.COMPLETED && this.completionDate == null) {
            this.completionDate = LocalDate.now();
            this.currentProgress = 100; // Films are complete at 100%
        }
    }
    
    public void setCurrentProgress(int currentProgress) {
        if (currentProgress < 0 || currentProgress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        this.currentProgress = currentProgress;
        this.lastUpdated = LocalDateTime.now();
        
        // Auto-update status based on progress
        if (currentProgress == 0 && this.status != Status.PLAN_TO_START) {
            this.status = Status.PLAN_TO_START;
        } else if (currentProgress > 0 && currentProgress < 100 && this.status != Status.IN_PROGRESS) {
            this.status = Status.IN_PROGRESS;
            if (this.startDate == null) {
                this.startDate = LocalDate.now();
            }
        } else if (currentProgress == 100 && this.status != Status.COMPLETED) {
            this.status = Status.COMPLETED;
            if (this.completionDate == null) {
                this.completionDate = LocalDate.now();
            }
        }
    }
    
    public void setRating(Double rating) {
        if (rating != null && (rating < 1.0 || rating > 5.0)) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        this.rating = rating;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
        }
    }
    
    public void setTopic(Topic topic) {
        this.topic = topic;
        if (topic != null) {
            this.topicId = topic.getTopicId();
        }
    }
    
    // Utility methods
    public boolean isCompleted() {
        return status == Status.COMPLETED || currentProgress == 100;
    }
    
    public String getProgressDisplay() {
        if (topic != null && topic.getCategory() == Topic.Category.MOVIES) {
            return currentProgress + "%";
        }
        return String.valueOf(currentProgress);
    }
    
    public String getRatingDisplay() {
        if (rating == null) return "Not rated";
        return String.format("%.1f/5.0", rating);
    }
    
    @Override
    public String toString() {
        return String.format("UserProgress{id=%d, userId=%d, topicId=%d, status=%s, progress=%d%%, rating=%s}",
                progressId, userId, topicId, status.getDisplayName(), currentProgress,
                rating != null ? String.format("%.1f", rating) : "null");
    }
    
    // Display method for console output
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        if (topic != null) {
            sb.append(String.format("📽️  %s (%s)\n", topic.getTitle(), 
                    topic.getReleaseYear() != null ? topic.getReleaseYear() : "N/A"));
            sb.append(String.format("   Director: %s\n", topic.getDirector()));
        }
        
        sb.append(String.format("   Status: %s\n", status.getDisplayName()));
        sb.append(String.format("   Progress: %s\n", getProgressDisplay()));
        
        if (rating != null) {
            sb.append(String.format("   Your Rating: %s ⭐\n", getRatingDisplay()));
        }
        
        if (startDate != null) {
            sb.append(String.format("   Started: %s\n", startDate.format(dateFormatter)));
        }
        
        if (completionDate != null) {
            sb.append(String.format("   Completed: %s\n", completionDate.format(dateFormatter)));
        }
        
        if (notes != null && !notes.trim().isEmpty()) {
            sb.append(String.format("   Notes: %s\n", notes));
        }
        
        sb.append(String.format("   Last Updated: %s", 
                lastUpdated.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
        
        return sb.toString();
    }
}
