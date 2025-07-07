package com.cognixia.jump.dao;

import com.cognixia.jump.model.Topic;
import java.util.List;
import java.util.Optional;

/**
 * TOPIC DAO INTERFACE
 * 
 * Data Access Object for Topic entities (sci-fi films).
 * Defines all database operations for Topic objects.
 */
public interface TopicDAO {

    /**
     * CREATE - Add a new topic to the database
     */
    Topic createTopic(Topic topic) throws Exception;

    /**
     * READ - Find topic by ID
     * 
     * Returns an Optional that either contains a Topic if found, or is empty if not found.
     */
    Optional<Topic> findById(int topicId);

    /**
     * READ - Find topics by title (partial match)
     */
    List<Topic> findByTitle(String titleSearch);

    /**
     * READ - Get all topics
     */
    List<Topic> getAllTopics();

    /**
     * READ - Get topics by category
     */
    List<Topic> getTopicsByCategory(Topic.Category category);

    /**
     * READ - Get top rated topics
     */
    List<Topic> getTopRatedTopics(int limit);

    /**
     * UPDATE - Modify existing topic
     * 
     * Returns true if topic was updated, false if not found or update failed.
     */
    boolean updateTopic(Topic topic);

    /**
     * DELETE - Remove topic by ID
     * 
     * Returns true if topic was deleted, false if not found or delete failed.
     */
    boolean deleteTopic(int topicId);

    /**
     * UTILITY - Get topic count
     */
    int getTopicCount();
}

