-- Progress Tracker Database Setup Script -Sci Fi Films
-- This script creates the database and tables needed for the application

-- Drop existing database if it exists and create new one
DROP DATABASE IF EXISTS progress_tracker_db;
CREATE DATABASE progress_tracker_db;
USE progress_tracker_db;

-- USER TABLE: Stores user authentication and profile information
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TOPIC TABLE: Stores available sci-fi films users can track
CREATE TABLE topic (
    topic_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    category ENUM('MOVIES') NOT NULL DEFAULT 'MOVIES',
    description TEXT,
    runtime_minutes INT DEFAULT NULL, -- Film runtime in minutes
    release_year YEAR,
    genre VARCHAR(100),
    director VARCHAR(150),           -- Film director
    letterboxd_rating DECIMAL(2,1),  -- Average rating from Letterboxd
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- USER_PROGRESS TABLE: Tracks each user's progress on films
-- This is the junction table between users and films
CREATE TABLE user_progress (
    progress_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    topic_id INT NOT NULL,
    status ENUM('PLAN_TO_START', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'PLAN_TO_START',
    current_progress INT DEFAULT 0,   -- For films: 0 (not started) or 100 (completed)
    rating DECIMAL(2,1) DEFAULT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    notes TEXT,
    start_date DATE DEFAULT NULL,
    completion_date DATE DEFAULT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints ensure data integrity
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topic(topic_id) ON DELETE CASCADE,
    
    -- Unique constraint prevents duplicate entries for same user/film combination
    UNIQUE KEY unique_user_topic (user_id, topic_id)
);

-- Insert sample users for testing
INSERT INTO user (username, password, email) VALUES 
('john_doe', 'password123', 'john@email.com'),
('jane_smith', 'securepass', 'jane@email.com'),
('admin', 'admin123', 'admin@email.com');

-- Insert the top 10 highly-rated sci-fi films from Letterboxd
INSERT INTO topic (title, category, description, runtime_minutes, release_year, genre, director, letterboxd_rating) VALUES 

('2001: A Space Odyssey', 'MOVIES', 'A voyage to Jupiter with the sentient computer HAL after the discovery of a mysterious monolith affecting human evolution.', 149, 1968, 'Sci-Fi', 'Stanley Kubrick', 4.2),

('Blade Runner 2049', 'MOVIES', 'Young Blade Runner K discovers a long-buried secret that leads him to track down former Blade Runner Rick Deckard.', 164, 2017, 'Sci-Fi', 'Denis Villeneuve', 4.3),

('The Matrix', 'MOVIES', 'When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth about reality.', 136, 1999, 'Sci-Fi', 'Lana Wachowski, Lilly Wachowski', 4.1),

('Arrival', 'MOVIES', 'A linguist works with the military to communicate with alien lifeforms after twelve mysterious spacecraft appear around the world.', 116, 2016, 'Sci-Fi', 'Denis Villeneuve', 4.2),

('Interstellar', 'MOVIES', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 169, 2014, 'Sci-Fi', 'Christopher Nolan', 4.3),

('Ex Machina', 'MOVIES', 'A young programmer is selected to participate in a ground-breaking experiment in synthetic intelligence.', 108, 2014, 'Sci-Fi', 'Alex Garland', 4.1),

('Her', 'MOVIES', 'In a near future, a lonely writer develops an unlikely relationship with an operating system designed to meet his every need.', 126, 2013, 'Sci-Fi', 'Spike Jonze', 4.0),

('Minority Report', 'MOVIES', 'In a future society, a special police unit is able to arrest murderers before they commit their crimes.', 145, 2002, 'Sci-Fi', 'Steven Spielberg', 3.9),

('Solaris', 'MOVIES', 'A psychologist is sent to a space station orbiting a planet whose ocean surface exhibits strange phenomena.', 167, 1972, 'Sci-Fi', 'Andrei Tarkovsky', 4.0),

('Stalker', 'MOVIES', 'A guide leads two men through an area known as the Zone to find a room that grants wishes.', 162, 1979, 'Sci-Fi', 'Andrei Tarkovsky', 4.1);

-- Insert sample progress data for testing
INSERT INTO user_progress (user_id, topic_id, status, current_progress, rating, notes) VALUES 

-- John's progress (sci-fi enthusiast)
(1, 1, 'COMPLETED', 100, 5.0, 'Kubrick''s masterpiece. A true cinematic experience that transcends genre.'),
(1, 3, 'COMPLETED', 100, 4.5, 'Mind-bending and revolutionary. Changed how I think about reality.'),
(1, 5, 'COMPLETED', 100, 4.0, 'Nolan at his best. Emotional and scientifically fascinating.'),
(1, 6, 'IN_PROGRESS', 50, NULL, 'Halfway through. The AI conversations are incredibly well done.'),
(1, 9, 'PLAN_TO_START', 0, NULL, 'Been meaning to watch this Tarkovsky classic for ages.'),

-- Jane's progress (casual sci-fi viewer)
(2, 2, 'COMPLETED', 100, 4.5, 'Visually stunning sequel that honors the original perfectly.'),
(2, 4, 'COMPLETED', 100, 4.0, 'Beautiful and thought-provoking. Amy Adams was incredible.'),
(2, 7, 'COMPLETED', 100, 3.5, 'Interesting concept but a bit slow for my taste.'),
(2, 8, 'IN_PROGRESS', 75, NULL, 'Almost finished. The future crime prediction is fascinating.'),
(2, 10, 'PLAN_TO_START', 0, NULL, 'Friend recommended this. Another Tarkovsky film to explore.');

-- Display the created tables structure
DESCRIBE user;
DESCRIBE topic; 
DESCRIBE user_progress;

-- Show sample data
SELECT 'USERS:' as 'TABLE';
SELECT * FROM user;

SELECT 'SCI-FI FILMS:' as 'TABLE';  
SELECT topic_id, title, director, release_year, letterboxd_rating, runtime_minutes FROM topic ORDER BY letterboxd_rating DESC;

SELECT 'USER PROGRESS:' as 'TABLE';
SELECT up.user_id, u.username, t.title, up.status, up.current_progress, up.rating, up.notes 
FROM user_progress up 
JOIN user u ON up.user_id = u.user_id 
JOIN topic t ON up.topic_id = t.topic_id 
ORDER BY u.username, t.title;