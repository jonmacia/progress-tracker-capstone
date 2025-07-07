package com.cognixia.jump.dao;

import com.cognixia.jump.model.Topic;
import com.cognixia.jump.connection.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TOPIC DAO IMPLEMENTATION
 * 
 * Concrete implementation of TopicDAO for sci-fi films.
 * Handles all CRUD operations for Topic entities using JDBC.
 */
public class TopicDAOImpl implements TopicDAO {

    private final ConnectionManager connectionManager;

    public TopicDAOImpl() {
        this.connectionManager = ConnectionManager.getInstance();
    }

    /**
     * CREATE TOPIC
     */
    @Override
    public Topic createTopic(Topic topic) throws Exception {
        String sql = "INSERT INTO topic (title, category, description, runtime_minutes, "
           + "release_year, genre, director, letterboxd_rating) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, topic.getTitle());
            pstmt.setString(2, topic.getCategory().name());
            pstmt.setString(3, topic.getDescription());
            pstmt.setObject(4, topic.getRuntimeMinutes());
            pstmt.setObject(5, topic.getReleaseYear() != null ? topic.getReleaseYear().getValue() : null);
            pstmt.setString(6, topic.getGenre());
            pstmt.setString(7, topic.getDirector());
            pstmt.setObject(8, topic.getLetterboxdRating());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating topic failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    topic.setTopicId(generatedKeys.getInt(1));
                    return topic;
                } else {
                    throw new SQLException("Creating topic failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating topic: " + e.getMessage());
            throw new Exception("Failed to create topic: " + e.getMessage(), e);
        }
    }

    /**
     * FIND BY ID
     * 
     * Returns an Optional of Topic if found, or Optional.empty() if no match.
     */
    @Override
    public Optional<Topic> findById(int topicId) {
        String sql = "SELECT * FROM topic WHERE topic_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, topicId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(extractTopicFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding topic by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * FIND BY TITLE
     * 
     * Search topics using partial match on title (case-insensitive)
     */
    @Override
    public List<Topic> findByTitle(String titleSearch) {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topic WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + titleSearch + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding topics by title: " + e.getMessage());
        }

        return topics;
    }

    /**
     * GET ALL TOPICS
     */
    @Override
    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topic ORDER BY topic_id";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all topics: " + e.getMessage());
        }

        return topics;
    }

    /**
     * GET TOPICS BY CATEGORY
     */
    @Override
    public List<Topic> getTopicsByCategory(Topic.Category category) {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topic WHERE category = ? ORDER BY title";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting topics by category: " + e.getMessage());
        }

        return topics;
    }

    /**
     * GET TOP RATED TOPICS
     */
    @Override
    public List<Topic> getTopRatedTopics(int limit) {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topic ORDER BY letterboxd_rating DESC LIMIT ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting top rated topics: " + e.getMessage());
        }

        return topics;
    }

    /**
     * UPDATE TOPIC
     * 
     * If the topic exists, updates it and returns true.
     * If not found, logs and returns false.
     */
    @Override
    public boolean updateTopic(Topic topic) {
        String sql = "UPDATE topic " +
             "SET title = ?, description = ?, runtime_minutes = ?, " +
             "release_year = ?, genre = ?, director = ?, letterboxd_rating = ? " +
             "WHERE topic_id = ?";


        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, topic.getTitle());
            pstmt.setString(2, topic.getDescription());
            pstmt.setObject(3, topic.getRuntimeMinutes());
            pstmt.setObject(4, topic.getReleaseYear() != null ? topic.getReleaseYear().getValue() : null);
            pstmt.setString(5, topic.getGenre());
            pstmt.setString(6, topic.getDirector());
            pstmt.setObject(7, topic.getLetterboxdRating());
            pstmt.setInt(8, topic.getTopicId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("No topic found with ID: " + topic.getTopicId());
                return false;
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error updating topic: " + e.getMessage());
            return false;
        }
    }

    /**
     * DELETE TOPIC
     * 
     * If the topic exists, deletes it and returns true.
     * If not found, logs and returns false.
     */
    @Override
    public boolean deleteTopic(int topicId) {
        String sql = "DELETE FROM topic WHERE topic_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, topicId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("No topic found with ID: " + topicId);
                return false;
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error deleting topic: " + e.getMessage());
            return false;
        }
    }

    /**
     * GET TOPIC COUNT
     * 
     * Returns total number of topics in the database.
     */
    @Override
    public int getTopicCount() {
        String sql = "SELECT COUNT(*) FROM topic";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting topic count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * HELPER METHOD - Extract Topic from ResultSet
     */
    private Topic extractTopicFromResultSet(ResultSet rs) throws SQLException {
        Topic topic = new Topic();

        topic.setTopicId(rs.getInt("topic_id"));
        topic.setTitle(rs.getString("title"));

        String categoryStr = rs.getString("category");
        if (categoryStr != null) {
            topic.setCategory(Topic.Category.valueOf(categoryStr));
        }

        topic.setDescription(rs.getString("description"));
        topic.setRuntimeMinutes((Integer) rs.getObject("runtime_minutes"));

        int year = rs.getInt("release_year");
        if (!rs.wasNull()) {
            topic.setReleaseYear(Year.of(year));
        }

        topic.setGenre(rs.getString("genre"));
        topic.setDirector(rs.getString("director"));

        // FIX: Properly convert BigDecimal to Double. Ran into issue here.
        //"Always remember to use getBigDecimal() for decimal columns and convert to the desired type using the appropriate method."
        BigDecimal rating = rs.getBigDecimal("letterboxd_rating");
        topic.setLetterboxdRating(rating != null ? rating.doubleValue() : null);

        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            topic.setCreatedDate(createdTimestamp.toLocalDateTime());
        }

        return topic;
    }
}
