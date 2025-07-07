package com.cognixia.jump.main;

import com.cognixia.jump.connection.ConnectionManager;
import com.cognixia.jump.dao.*;

import com.cognixia.jump.model.*;
import com.cognixia.jump.exception.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * PROGRESS TRACKER APPLICATION - MAIN CLASS
 * 
 * This is the entry point for the Progress Tracker console application.
 * Manages user authentication, menu navigation, and all user interactions.
 * 
 * Requirements Met:
 * - Console-based menu interface
 * - User authentication (username/password)
 * - Personal progress tracking (3 statuses)
 * - User-specific data access
 * - MySQL database integration via JDBC & DAO
 * - Custom exceptions implemented
 * - 10 topics available
 */
public class ProgressTrackerApp {
    
    // Static variables for application state
    private static Scanner scanner = new Scanner(System.in);
    private static ConnectionManager connectionManager;
    private static UserDAO userDAO;
    private static TopicDAO topicDAO;
    private static UserProgressDAO progressDAO;
    private static User currentUser = null;
    
    /**
     * MAIN METHOD - Application Entry Point
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   PROGRESS TRACKER - SCI-FI FILMS");
        System.out.println("========================================");
        
        try {
            // Initialize database connection and DAOs
            initializeApplication();
            
            // Start the authentication loop
            boolean exit = false;
            while (!exit) {
                if (currentUser == null) {
                    exit = showAuthenticationMenu();
                } else {
                    exit = showMainMenu();
                }
            }
            
            System.out.println("\nThank you for using Progress Tracker! Goodbye! 👋");
            
        } catch (Exception e) {
            System.err.println("❌ Critical error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            scanner.close();
            System.out.println("Application terminated.");
        }
    }
    
    /**
     * INITIALIZE APPLICATION
     */
    private static void initializeApplication() throws Exception {
        System.out.println("Initializing application...");
        
        // Get the singleton instance of ConnectionManager
        connectionManager = ConnectionManager.getInstance();
        
        // Test database connection
        if (!connectionManager.testConnection()) {
            throw new Exception("Cannot connect to database. Please check your MySQL setup.");
        }
        
        // Initialize DAOs
        userDAO = new UserDAOImpl();
        topicDAO = new TopicDAOImpl();
        progressDAO = new UserProgressDAOImpl();
        
        System.out.println("✅ Application initialized successfully!\n");
    }
    
    /**
     * AUTHENTICATION MENU
     */
    private static boolean showAuthenticationMenu() {
        System.out.println("\n📋 AUTHENTICATION MENU");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                return handleLogin();
            case "2":
                return handleRegistration();
            case "3":
                return true; // Exit application
            default:
                System.out.println("❌ Invalid option. Please try again.");
                return false;
        }
    }
    
    /**
     * HANDLE USER LOGIN
     */
    private static boolean handleLogin() {
        System.out.println("\n🔐 LOGIN");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Username and password cannot be empty.");
            return false;
        }
        
        try {
            Optional<User> userOpt = userDAO.authenticateUser(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("✅ Welcome back, " + currentUser.getUsername() + "!");
                return false; // Don't exit, show main menu
            } else {
                System.out.println("❌ Invalid username or password.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * HANDLE USER REGISTRATION
     */
    private static boolean handleRegistration() {
        System.out.println("\n📋 CREATE ACCOUNT");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Email (optional): ");
        String email = scanner.nextLine().trim();
        
        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Username and password are required.");
            return false;
        }
        
        if (password.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters long.");
            return false;
        }
        
        try {
            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                System.out.println("❌ Username already exists. Please choose a different username.");
                return false;
            }
            
            // Create new user
            User newUser = new User(username, password, email.isEmpty() ? null : email);
            userDAO.createUser(newUser);
            
            System.out.println("✅ Account created successfully! You can now login.");
            return false; // Don't exit, let them login
            
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * MAIN MENU (After Authentication)
     */
    private static boolean showMainMenu() {
        System.out.println("\n🎬 MAIN MENU");
        System.out.println("1. Browse Films");
        System.out.println("2. My Progress");
        System.out.println("3. Add Film to Tracking");
        System.out.println("4. Update Progress");
        System.out.println("5. View Film Statistics");
        System.out.println("6. Account Settings");
        System.out.println("7. Logout");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                browseFilms();
                return false;
            case "2":
                viewMyProgress();
                return false;
            case "3":
                addFilmToTracking();
                return false;
            case "4":
                updateProgress();
                return false;
            case "5":
                viewFilmStatistics();
                return false;
            case "6":
                accountSettings();
                return false;
            case "7":
                logout();
                return false;
            case "8":
                return true; // Exit application
            default:
                System.out.println("❌ Invalid option. Please try again.");
                return false;
        }
    }
    
    /**
     * BROWSE FILMS
     */
    private static void browseFilms() {
        System.out.println("\n📽️ BROWSE FILMS");
        
        try {
            List<Topic> topics = topicDAO.getAllTopics();
            
            if (topics.isEmpty()) {
                System.out.println("No films available.");
                return;
            }
            
            // Display films in a formatted table
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-3s | %-35s | %-4s | %-8s | %-15s | %-4s%n", 
                "ID", "Title", "Year", "Category", "Letterboxd", "Dur.");
            System.out.println("-".repeat(100));
            
            for (Topic topic : topics) {
                System.out.printf("%-3d | %-35s | %-4s | %-8s | ⭐ %.1f         | %d min%n",
                    topic.getTopicId(),
                    truncate(topic.getTitle(), 35),
                    topic.getReleaseYear() != null ? topic.getReleaseYear().getValue() : "N/A",
                    topic.getCategory().name(),
                    topic.getLetterboxdRating(),
                    topic.getRuntimeMinutes()
                );
            }
            System.out.println("-".repeat(100));
            
        } catch (Exception e) {
            System.out.println("❌ Error loading films: " + e.getMessage());
        }
    }
    
    /**
     * VIEW MY PROGRESS
     */
    private static void viewMyProgress() {
        System.out.println("\n📊 MY PROGRESS");
        
        try {
            // Get user's progress entries
            List<UserProgress> progressList = progressDAO.getUserProgress(currentUser.getUserId());
            
            if (progressList.isEmpty()) {
                System.out.println("You haven't started tracking any films yet.");
                return;
            }
            
            // Get summary statistics
            UserProgressSummary summary = progressDAO.getUserProgressSummary(currentUser.getUserId());
            
            // Display summary
            System.out.println("\n📈 Summary:");
            System.out.println("Total Films Tracked: " + summary.totalTracking);
            System.out.println("Plan to Start: " + summary.planToStart);
            System.out.println("In Progress: " + summary.inProgress);
            System.out.println("Completed: " + summary.completed);
            
            // Group progress by status
            System.out.println("\n🎯 PLAN TO START:");
            displayProgressByStatus(progressList, UserProgress.Status.PLAN_TO_START);
            
            System.out.println("\n⏳ IN PROGRESS:");
            displayProgressByStatus(progressList, UserProgress.Status.IN_PROGRESS);
            
            System.out.println("\n✅ COMPLETED:");
            displayProgressByStatus(progressList, UserProgress.Status.COMPLETED);
            
        } catch (Exception e) {
            System.out.println("❌ Error loading progress: " + e.getMessage());
        }
    }
    
    /**
     * HELPER - Display progress entries by status
     */
    private static void displayProgressByStatus(List<UserProgress> progressList, UserProgress.Status status) {
        boolean found = false;
        for (UserProgress progress : progressList) {
            if (progress.getStatus() == status) {
                found = true;
                Topic topic = progress.getTopic();
                
                System.out.printf("  • %s (%s)", topic.getTitle(), 
                    topic.getReleaseYear() != null ? topic.getReleaseYear().getValue() : "N/A");
                
                if (status == UserProgress.Status.IN_PROGRESS) {
                    System.out.printf(" - %d%% complete", progress.getCurrentProgress());
                } else if (status == UserProgress.Status.COMPLETED && progress.getRating() != null) {
                    System.out.printf(" - Your rating: %.1f⭐", progress.getRating());
                }
                
                System.out.println();
            }
        }
        
        if (!found) {
            System.out.println("  None");
        }
    }
    
    /**
     * ADD FILM TO TRACKING
     */
    private static void addFilmToTracking() {
        System.out.println("\n➕ ADD FILM TO TRACKING");
        
        browseFilms(); // Show available films
        
        System.out.print("\nEnter Film ID to add (0 to cancel): ");
        
        try {
            int topicId = Integer.parseInt(scanner.nextLine().trim());
            
            if (topicId == 0) {
                return;
            }
            
            // Check if film exists
            Optional<Topic> topicOpt = topicDAO.findById(topicId);
            if (topicOpt.isEmpty()) {
                System.out.println("❌ Film not found.");
                return;
            }
            
            Topic topic = topicOpt.get();
            
            // Check if already tracking
            if (progressDAO.isUserTrackingTopic(currentUser.getUserId(), topicId)) {
                System.out.println("❌ You are already tracking this film.");
                return;
            }
            
            // Choose initial status
            System.out.println("\nSelect initial status:");
            System.out.println("1. Plan to Start");
            System.out.println("2. In Progress");
            System.out.println("3. Completed");
            System.out.print("Choice: ");
            
            String statusChoice = scanner.nextLine().trim();
            UserProgress.Status status;
            
            switch (statusChoice) {
                case "1":
                    status = UserProgress.Status.PLAN_TO_START;
                    break;
                case "2":
                    status = UserProgress.Status.IN_PROGRESS;
                    break;
                case "3":
                    status = UserProgress.Status.COMPLETED;
                    break;
                default:
                    System.out.println("❌ Invalid choice. Defaulting to 'Plan to Start'.");
                    status = UserProgress.Status.PLAN_TO_START;
            }
            
            // Create progress entry
            UserProgress progress = new UserProgress(currentUser.getUserId(), topicId, status);
            
            // If completed, ask for rating
            if (status == UserProgress.Status.COMPLETED) {
                System.out.print("Rate this film (1.0-5.0): ");
                try {
                    double rating = Double.parseDouble(scanner.nextLine().trim());
                    if (rating >= 1.0 && rating <= 5.0) {
                        progress.setRating(rating);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid rating
                }
            }
            
            progressDAO.createProgress(progress);
            System.out.println("✅ '" + topic.getTitle() + "' added to your tracking list!");
            
        } catch (ProgressAlreadyExistsException e) {
            System.out.println("❌ You are already tracking this film.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid film ID.");
        } catch (Exception e) {
            System.out.println("❌ Error adding film: " + e.getMessage());
        }
    }
    
    /**
     * UPDATE PROGRESS
     */
    private static void updateProgress() {
        System.out.println("\n📝 UPDATE PROGRESS");
        
        try {
            // Get user's in-progress and plan-to-start films
            List<UserProgress> progressList = progressDAO.getUserProgress(currentUser.getUserId());
            List<UserProgress> updateable = new ArrayList<>();
            
            for (UserProgress p : progressList) {
                if (p.getStatus() != UserProgress.Status.COMPLETED) {
                    updateable.add(p);
                }
            }
            
            if (updateable.isEmpty()) {
                System.out.println("No films to update. All your tracked films are completed.");
                return;
            }
            
            // Display updateable films
            System.out.println("\nYour films:");
            for (int i = 0; i < updateable.size(); i++) {
                UserProgress p = updateable.get(i);
                Topic t = p.getTopic();
                System.out.printf("%d. %s (%s) - %s%n", 
                    i + 1, t.getTitle(), 
                    t.getReleaseYear() != null ? t.getReleaseYear().toString() : "N/A", 
                    p.getStatus().getDisplayName());
            }
            
            System.out.print("\nSelect film to update (0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (choice < 0 || choice >= updateable.size()) {
                return;
            }
            
            UserProgress selected = updateable.get(choice);
            Topic topic = selected.getTopic();
            
            // Update menu
            System.out.println("\nUpdating: " + topic.getTitle());
            System.out.println("Current Status: " + selected.getStatus().getDisplayName());
            System.out.println("\n1. Change Status");
            System.out.println("2. Add/Edit Notes");
            System.out.println("3. Cancel");
            System.out.print("Choice: ");
            
            String updateChoice = scanner.nextLine().trim();
            
            switch (updateChoice) {
                case "1":
                    updateStatus(selected);
                    break;
                case "2":
                    updateNotes(selected);
                    break;
                default:
                    return;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error updating progress: " + e.getMessage());
        }
    }
    
    /**
     * UPDATE STATUS
     */
    private static void updateStatus(UserProgress progress) {
        System.out.println("\nSelect new status:");
        System.out.println("1. Plan to Start");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        UserProgress.Status newStatus;
        
        switch (choice) {
            case "1":
                newStatus = UserProgress.Status.PLAN_TO_START;
                break;
            case "2":
                newStatus = UserProgress.Status.IN_PROGRESS;
                break;
            case "3":
                newStatus = UserProgress.Status.COMPLETED;
                break;
            default:
                System.out.println("❌ Invalid choice.");
                return;
        }
        
        progress.setStatus(newStatus);
        
        // If completed, ask for rating
        if (newStatus == UserProgress.Status.COMPLETED) {
            System.out.print("Rate this film (1.0-5.0): ");
            try {
                double rating = Double.parseDouble(scanner.nextLine().trim());
                if (rating >= 1.0 && rating <= 5.0) {
                    progress.setRating(rating);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid rating
            }
            progress.setCurrentProgress(100);
            progress.setCompletionDate(LocalDate.now());
        }
        
        if (progressDAO.updateProgress(progress)) {
            System.out.println("✅ Status updated successfully!");
        } else {
            System.out.println("❌ Failed to update status.");
        }
    }
    
    /**
     * UPDATE NOTES
     */
    private static void updateNotes(UserProgress progress) {
        System.out.println("\nCurrent notes: " + 
            (progress.getNotes() != null ? progress.getNotes() : "None"));
        System.out.print("Enter new notes (or press Enter to keep current): ");
        
        String notes = scanner.nextLine().trim();
        
        if (!notes.isEmpty()) {
            if (progressDAO.updateNotes(progress.getProgressId(), notes)) {
                System.out.println("✅ Notes updated successfully!");
            } else {
                System.out.println("❌ Failed to update notes.");
            }
        }
    }
    
    /**
     * VIEW FILM STATISTICS
     */
    private static void viewFilmStatistics() {
        System.out.println("\n📊 FILM STATISTICS");
        
        browseFilms(); // Show available films
        
        System.out.print("\nEnter Film ID to view stats (0 to cancel): ");
        
        try {
            int topicId = Integer.parseInt(scanner.nextLine().trim());
            
            if (topicId == 0) {
                return;
            }
            
            // Get film details
            Optional<Topic> topicOpt = topicDAO.findById(topicId);
            if (topicOpt.isEmpty()) {
                System.out.println("❌ Film not found.");
                return;
            }
            
            Topic topic = topicOpt.get();
            TopicStats stats = progressDAO.getTopicStatistics(topicId);
            
            // Display statistics
            System.out.println("\n📈 Statistics for: " + topic.getTitle());
            System.out.println("Total Users Tracking: " + stats.totalUsers);
            System.out.println("Plan to Start: " + stats.planToStartCount);
            System.out.println("In Progress: " + stats.inProgressCount);
            System.out.println("Completed: " + stats.completedCount);
            
            if (stats.averageRating > 0) {
                System.out.printf("Average User Rating: %.1f⭐%n", stats.averageRating);
            } else {
                System.out.println("Average User Rating: No ratings yet");
            }
            
            System.out.printf("Letterboxd Rating: %.1f⭐%n", topic.getLetterboxdRating());
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid film ID.");
        } catch (Exception e) {
            System.out.println("❌ Error loading statistics: " + e.getMessage());
        }
    }
    
    /**
     * ACCOUNT SETTINGS
     */
    private static void accountSettings() {
        System.out.println("\n⚙️ ACCOUNT SETTINGS");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + (currentUser.getEmail() != null ? 
            currentUser.getEmail() : "Not provided"));
        System.out.println("Member since: " + currentUser.getCreatedDate()
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        System.out.println("\n1. Change Password");
        System.out.println("2. Update Email");
        System.out.println("3. View Progress Summary");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                changePassword();
                break;
            case "2":
                updateEmail();
                break;
            case "3":
                viewProgressSummary();
                break;
            default:
                return;
        }
    }
    
    /**
     * CHANGE PASSWORD
     */
    private static void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();
        
        // Verify current password
        Optional<User> verified = userDAO.authenticateUser(
            currentUser.getUsername(), currentPassword);
        
        if (verified.isEmpty()) {
            System.out.println("❌ Current password is incorrect.");
            return;
        }
        
        System.out.print("Enter new password (min 6 chars): ");
        String newPassword = scanner.nextLine().trim();
        
        if (newPassword.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters long.");
            return;
        }
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords do not match.");
            return;
        }
        
        try {
            if (userDAO.updatePassword(currentUser.getUserId(), newPassword)) {
                System.out.println("✅ Password changed successfully!");
            } else {
                System.out.println("❌ Failed to change password.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error changing password: " + e.getMessage());
        }
    }
    
    /**
     * UPDATE EMAIL
     */
    private static void updateEmail() {
        System.out.println("Current email: " + 
            (currentUser.getEmail() != null ? currentUser.getEmail() : "Not set"));
        System.out.print("Enter new email (or press Enter to remove): ");
        
        String newEmail = scanner.nextLine().trim();
        
        try {
            currentUser.setEmail(newEmail.isEmpty() ? null : newEmail);
            if (userDAO.updateUser(currentUser)) {
                System.out.println("✅ Email updated successfully!");
            } else {
                System.out.println("❌ Failed to update email.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error updating email: " + e.getMessage());
        }
    }
    
    /**
     * VIEW PROGRESS SUMMARY
     */
    private static void viewProgressSummary() {
        try {
            UserProgressSummary summary = progressDAO.getUserProgressSummary(currentUser.getUserId());
            
            System.out.println("\n📊 YOUR PROGRESS SUMMARY");
            System.out.println("Total Films Tracked: " + summary.totalTracking);
            System.out.println("Plan to Start: " + summary.planToStart);
            System.out.println("In Progress: " + summary.inProgress);
            System.out.println("Completed: " + summary.completed);
            
            if (summary.completed > 0) {
                double completionRate = (summary.completed * 100.0) / summary.totalTracking;
                System.out.printf("Completion Rate: %.1f%%%n", completionRate);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error loading summary: " + e.getMessage());
        }
    }
    
    /**
     * LOGOUT
     */
    private static void logout() {
        System.out.println("Logging out...");
        currentUser = null;
        System.out.println("✅ Logged out successfully!");
    }
    
    /**
     * HELPER - Truncate string to specified length
     */
    private static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}