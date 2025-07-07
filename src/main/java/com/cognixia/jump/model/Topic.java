package com.cognixia.jump.model;

import java.time.LocalDateTime;
import java.time.Year;

/**
 * TOPIC MODEL CLASS 
 * 
 * This class represents a Topic (sci-fi film) in the Progress Tracker system.
 * It maps directly to the 'topic' table in the database.
 * 
 * Updated to handle sci-fi films with Letterboxd ratings
 */
public class Topic {
    
    // Fields matching database columns
    private int topicId;                // Primary key
    private String title;               // Film title
    private Category category;          
    private String description;         // Film synopsis
    private Integer runtimeMinutes;     // Film runtime in minutes
    private Year releaseYear;           // Year film was released
    private String genre;               
    private String director;            // Film director
    private Double letterboxdRating;    // Average rating from Letterboxd (1.0-5.0)
    private LocalDateTime createdDate;  // When added to database
    
    // Enum for category 
    public enum Category {
        MOVIES
    }
    
    // Default constructor
    public Topic() {
        this.category = Category.MOVIES;
        this.genre = "Sci-Fi";
        this.createdDate = LocalDateTime.now();
    }
    
    // Constructor for creating new film topic
    public Topic(String title, String description, Integer runtimeMinutes, 
                 Year releaseYear, String director, Double letterboxdRating) {
        this();
        this.title = title;
        this.description = description;
        this.runtimeMinutes = runtimeMinutes;
        this.releaseYear = releaseYear;
        this.director = director;
        this.letterboxdRating = letterboxdRating;
    }
    
    // Full constructor - used when loading from database
    public Topic(int topicId, String title, Category category, String description,
                 Integer runtimeMinutes, Year releaseYear, String genre, 
                 String director, Double letterboxdRating, LocalDateTime createdDate) {
        this.topicId = topicId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.runtimeMinutes = runtimeMinutes;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.director = director;
        this.letterboxdRating = letterboxdRating;
        this.createdDate = createdDate;
    }
    
    // Getters
    public int getTopicId() {
        return topicId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }
    
    public Year getReleaseYear() {
        return releaseYear;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public String getDirector() {
        return director;
    }
    
    public Double getLetterboxdRating() {
        return letterboxdRating;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    // Setters with validation
    public void setTopicId(int topicId) {
        if (topicId < 0) {
            throw new IllegalArgumentException("Topic ID cannot be negative");
        }
        this.topicId = topicId;
    }
    
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Title cannot exceed 255 characters");
        }
        this.title = title.trim();
    }
    
    public void setCategory(Category category) {
        this.category = category != null ? category : Category.MOVIES;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setRuntimeMinutes(Integer runtimeMinutes) {
        if (runtimeMinutes != null && runtimeMinutes < 0) {
            throw new IllegalArgumentException("Runtime cannot be negative");
        }
        this.runtimeMinutes = runtimeMinutes;
    }
    
    public void setReleaseYear(Year releaseYear) {
        this.releaseYear = releaseYear;
    }
    
    public void setGenre(String genre) {
        if (genre != null && genre.length() > 100) {
            throw new IllegalArgumentException("Genre cannot exceed 100 characters");
        }
        this.genre = genre;
    }
    
    public void setDirector(String director) {
        if (director != null && director.length() > 150) {
            throw new IllegalArgumentException("Director name cannot exceed 150 characters");
        }
        this.director = director;
    }
    
    public void setLetterboxdRating(Double letterboxdRating) {
        if (letterboxdRating != null && (letterboxdRating < 1.0 || letterboxdRating > 5.0)) {
            throw new IllegalArgumentException("Letterboxd rating must be between 1.0 and 5.0");
        }
        this.letterboxdRating = letterboxdRating;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // Utility methods
    public String getFormattedRuntime() {
        if (runtimeMinutes == null) return "Unknown";
        int hours = runtimeMinutes / 60;
        int minutes = runtimeMinutes % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }
    
    public String getProgressUnit() {
        return "completion percentage";
    }
    
    public int getMaxProgress() {
        return 100; // Films are tracked as percentage (0-100)
    }
    
    @Override
    public String toString() {
        return String.format("Topic{id=%d, title='%s', director='%s', year=%s, rating=%.1f, runtime=%s}",
                topicId, title, director, 
                releaseYear != null ? releaseYear.toString() : "Unknown",
                letterboxdRating != null ? letterboxdRating : 0.0,
                getFormattedRuntime());
    }
    
    // Display method for console output
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("📽️  %s (%s)\n", title, releaseYear != null ? releaseYear : "N/A"));
        sb.append(String.format("   Director: %s\n", director != null ? director : "Unknown"));
        sb.append(String.format("   Runtime: %s\n", getFormattedRuntime()));
        sb.append(String.format("   Letterboxd Rating: %.1f/5.0\n", letterboxdRating != null ? letterboxdRating : 0.0));
        if (description != null && !description.isEmpty()) {
            // Wrap description at 70 characters
            String wrapped = wrapText(description, 70);
            sb.append("   Synopsis: ").append(wrapped.replace("\n", "\n   "));
        }
        return sb.toString();
    }
    
    // Helper method to wrap text
    private String wrapText(String text, int maxWidth) {
        if (text.length() <= maxWidth) return text;
        
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            if (line.length() + word.length() + 1 > maxWidth) {
                wrapped.append(line).append("\n");
                line = new StringBuilder();
            }
            if (line.length() > 0) line.append(" ");
            line.append(word);
        }
        if (line.length() > 0) wrapped.append(line);
        
        return wrapped.toString();
    }
}
