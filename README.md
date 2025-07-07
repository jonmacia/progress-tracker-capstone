# Progress Tracker - Java Backend Capstone Project

A console-based Java application for sci-fi film enthusiasts to track their viewing progress. Users can organize films into three categories: "Plan to Start," "In Progress," and "Completed," with additional features like ratings and personal notes.

## 🎯 Project Overview

This capstone project demonstrates entry-level Java backend development skills including:

- **Object-Oriented Programming** with proper encapsulation and abstraction
- **Database Design** with MySQL and proper normalization
- **JDBC & DAO Pattern** for clean separation of data access logic
- **Custom Exception Handling** for robust error management
- **Maven Project Management** for dependency handling
- **Git Version Control** 
- **Console Interface** with user-friendly menu navigation

## ✅ Requirements Met

### Core Requirements ✓
- ✓ Console-based menu interface
- ✓ User authentication (login with username/password)
- ✓ Personal progress tracking (plan to start/in progress/completed)
- ✓ User-specific data access and security
- ✓ MySQL database with proper ER design
- ✓ JDBC & DAO pattern implementation
- ✓ 2 custom exceptions
- ✓ 10 topics available for tracking
- ✓ Git version control ready

### Extensions Implemented ✓
- ✓ Maven project structure
- ✓ Film runtime tracking
- ✓ User rating system (1.0-5.0 stars)
- ✓ Film statistics (by director, year, rating)
- ✓ Account creation functionality
- ✓ Search films by title/director
- ✓ Letterboxd ratings integration

## 💾 Database Design

### ER Diagram Overview
```
USER (1) ←→ (N) USER_PROGRESS (N) ←→ (1) TOPIC
```

### Tables

1. **user** - Stores user accounts and authentication
   - `user_id` (Primary Key)
   - `username`, `password`, `email`

2. **topic** - Contains sci-fi films to track
   - `topic_id` (Primary Key)
   - `title`, `director`, `release_year`
   - `runtime_minutes`, `letterboxd_rating`
   - `description`, `genre`

3. **user_progress** - Junction table tracking user's progress
   - `progress_id` (Primary Key)
   - `user_id` (Foreign Key), `topic_id` (Foreign Key)
   - `status` (PLAN_TO_START, IN_PROGRESS, COMPLETED)
   - `current_progress` (0 or 100 for films)
   - `rating`, `notes`
   - `start_date`, `completion_date`

### Sample Data Included
- 10 highly-rated sci-fi films from Letterboxd
- Films ranging from classics (2001: A Space Odyssey) to modern (Blade Runner 2049)
- Test users with sample viewing progress
- Directors include Kubrick, Tarkovsky, Villeneuve, and more

## 🚀 Setup Instructions

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd progress-tracker
```

### 2. Database Setup

#### Using MySQL Workbench (GUI):
1. Open MySQL Workbench
2. File → Open SQL Script → Select `src/main/resources/database_setup.sql`
3. Execute the script

#### Using Command Line:
```bash
# Windows
mysql -u root -p < src\main\resources\database_setup.sql

# Mac/Linux
mysql -u root -p < src/main/resources/database_setup.sql
```

### 3. Configure Database Connection
Edit `src/main/java/com/cognixia/jump/connection/ConnectionManager.java`:
```java
private static final String USERNAME = "root";           // Your MySQL username
private static final String PASSWORD = "your_password";  // Your MySQL password
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn exec:java
```

## 🖥️ Runtime Instructions

### Starting the Application

#### Option 1: Maven (Recommended)
```bash
mvn exec:java
```

#### Option 2: Java Command
```bash
java -cp target/classes:lib/* com.cognixia.jump.main.ProgressTrackerApp
```

### Application Flow

When you start the application, you'll see:
```
========================================
   PROGRESS TRACKER - SCI-FI FILMS
========================================
Initializing application...
✅ MySQL JDBC Driver loaded successfully
✅ Application initialized successfully!

📋 AUTHENTICATION MENU
1. Login
2. Create Account
3. Exit
Choose an option: 
```

### Navigation Guide

#### First Time Users:
1. Select **2** to create a new account
2. Enter username, password, and email
3. System will confirm registration
4. Select **1** to login with your new credentials

#### Returning Users:
1. Select **1** to login
2. Enter your username and password
3. Access the main menu

### Main Menu
After successful login:
```
🏠 MAIN MENU - Welcome, [username]!
1. Browse Films
2. My Progress
3. Add Film to Tracking
4. Update Progress
5. View Film Statistics
6. Account Settings
7. Logout
8. Exit
Choose an option: 
```

### Feature Walkthrough

#### 📽️ Browse Films (Option 1)
Displays all sci-fi films in a formatted table:
```
----------------------------------------------------------------------------------------------------
ID  | Title                               | Year | Category | Letterboxd | Dur.
----------------------------------------------------------------------------------------------------
1   | 2001: A Space Odyssey              | 1968 | MOVIES   | 4.2/5.0    | 149m
2   | Blade Runner 2049                  | 2017 | MOVIES   | 4.0/5.0    | 164m
...
```

#### 📊 My Progress (Option 2)
Shows your tracked films organized by status:
```
📊 MY PROGRESS SUMMARY

PLAN TO START (3):
- Stalker (1979) - Andrei Tarkovsky

IN PROGRESS (2):
- 2001: A Space Odyssey (1968) - Stanley Kubrick
  Progress: 50% | Started: 2024-01-15

COMPLETED (4):
- Blade Runner 2049 (2017) - Denis Villeneuve
  Rating: ⭐ 4.5 | Completed: 2024-01-10
  Notes: "Visually stunning sequel"
```

#### ➕ Add Film to Tracking (Option 3)
- Displays available films not in your list
- Enter film ID to add to your watchlist
- Automatically sets status to "Plan to Start"

#### 📝 Update Progress (Option 4)
- Shows your non-completed films
- Options to:
  - Change status (Plan to Start → In Progress → Completed)
  - Add/edit notes
  - Rate completed films (1.0-5.0)

#### 📈 View Film Statistics (Option 5)
Displays various statistics:
```
📈 FILM STATISTICS

Most Popular Films:
1. Interstellar - 15 users tracking
2. Blade Runner 2049 - 12 users tracking

Highest Rated Films:
1. 2001: A Space Odyssey - 4.2/5.0
2. Solaris - 4.1/5.0

Your Stats:
- Total films tracked: 9
- Completion rate: 44.4%
- Average rating: 4.3
```

#### ⚙️ Account Settings (Option 6)
- Change password
- Update email
- View account information

### Keyboard Navigation
- Enter numbers (1-8) to select menu options
- Press Enter after each input
- Invalid inputs prompt for retry
- Use menu numbers consistently throughout

### Error Handling
The application handles common errors:
- Invalid menu selections prompt for retry  
- Failed logins show "Invalid username or password"
- Duplicate film additions show "You are already tracking this film"
- Empty inputs are validated
- Database connection issues are reported with helpful messages

## 💻 Runtime Requirements

### System Requirements
- **Java Runtime**: JRE 11 or higher
- **Memory**: 512MB RAM minimum
- **Database**: MySQL server must be running
- **Network**: Localhost access

### Performance Expectations
- Application startup: < 3 seconds
- Database queries: < 1 second
- Menu navigation: Instantaneous

### Troubleshooting Runtime Issues

#### Application Won't Start
```bash
# Verify Java version
java -version

# Check MySQL service
# Linux/Mac
ps aux | grep mysql
# Windows
sc query MySQL80
```

#### Connection Errors
1. Verify MySQL is running
2. Check credentials in ConnectionManager.java
3. Ensure database exists: `mysql -u root -p -e "USE progress_tracker_db"`

#### Slow Performance
- Check MySQL server load
- Verify network connectivity to localhost
- Restart MySQL service if needed

## 🏗️ Project Structure
```
src/
├── main/java/com/cognixia/jump/
│   ├── main/
│   │   └── ProgressTrackerApp.java         # Main application entry
│   ├── model/
│   │   ├── User.java                       # User entity
│   │   ├── Topic.java                      # Topic entity
│   │   └── UserProgress.java               # Progress tracking entity
│   ├── dao/
│   │   ├── UserDAO.java & UserDAOImpl.java
│   │   ├── TopicDAO.java & TopicDAOImpl.java
│   │   └── UserProgressDAO.java & UserProgressDAOImpl.java
│   ├── exception/
│   │   ├── UserNotFoundException.java      # Custom exception
│   │   └── ProgressAlreadyExistsException.java # Custom exception
│   └── connection/
│       └── ConnectionManager.java          # Database connection
├── resources/
│   └── database_setup.sql                  # Database creation script
└── pom.xml                                # Maven configuration
```

## 🎮 How to Use

### Getting Started
1. Run the application using `mvn exec:java`
2. Choose "Register New Account" to create your account
3. Login with your credentials
4. Start tracking sci-fi films!

### Quick Start Example
```bash
# Start application
mvn exec:java

# At main menu, press 1 to register
# Enter: username, password, email
# Press 2 to login
# Start exploring sci-fi films!
```

### Main Features

#### 🔐 User Authentication
- Register new accounts with username/password
- Secure login system
- Account management (update password, email)

#### 🎬 Film Management
- Browse 10 pre-loaded sci-fi films
- View detailed film information (director, year, runtime, Letterboxd rating)
- Search films by title or director
- Filter by release year or rating

#### 📊 Progress Tracking
- Add films to your personal watchlist
- Set status: Plan to Start → In Progress → Completed
- Track viewing progress (0% not started, 100% completed)
- Add personal notes about each film
- Rate completed films (1.0-5.0 stars)

#### 📈 Statistics & Reports
- View your personal viewing dashboard
- See film statistics (most watched directors, years)
- Track completion rates
- Compare your ratings with Letterboxd averages
- View films by highest/lowest ratings

## 🎯 Sample Usage Flow
```
1. Start app: mvn exec:java
2. Select: 2 (Create Account)
3. Register: Enter username "scifi_fan", password, email
4. Select: 1 (Login)
5. Login: Use your new credentials
6. Main Menu: Select 1 to browse films
7. Select: 3 to add "Interstellar" to your watchlist
8. Select: 4 to update progress when you start watching
9. Select: 5 to view film statistics
10. Select: 7 to logout safely
```

## 🧪 Testing Data

The database includes sample data for testing:

**Test Accounts:**
- Username: `john_doe`, Password: `password123`
- Username: `jane_smith`, Password: `securepass`

**Sample Films:**
- Sci-Fi Classics: 2001: A Space Odyssey, Solaris, Stalker
- Modern Masterpieces: Blade Runner 2049, Arrival, Interstellar
- Indie Gems: Primer, Coherence, Under the Skin
- All films include Letterboxd ratings, runtime, and director info

## 🔧 Custom Exceptions

### UserNotFoundException
Thrown when:
- Login with invalid username
- Accessing non-existent user data

### ProgressAlreadyExistsException
Thrown when:
- User tries to track a film they're already tracking
- Duplicate watchlist entries

## 🛠️ Technical Highlights

### JDBC & DAO Pattern
- Interface-based design for flexibility
- PreparedStatements to prevent SQL injection
- Connection management singleton pattern
- Proper resource handling with try-with-resources

### Object-Oriented Design
- Encapsulation with private fields and public methods
- Custom exceptions extending Exception class
- DAO interfaces with implementations
- Clean separation of concerns

### Database Best Practices
- Normalized design (3NF)
- Foreign key constraints
- Enumerated types for status and category fields
- Auto-increment primary keys
- Additional film metadata (director, year, runtime, ratings)

## 📋 Git Workflow 

### Recommended Git Workflow
```bash
# Feature development
git checkout -b feature/user-authentication
git add .
git commit -m "Add user login functionality"
git push origin feature/user-authentication
# Create pull request on GitHub
```
## 🎓 Learning Objectives Achieved

### Java Backend Development
- ✓ Object-oriented programming principles
- ✓ Exception handling and custom exceptions
- ✓ Database connectivity with JDBC
- ✓ Design patterns (DAO, Singleton)
- ✓ Maven project management

### Database Skills
- ✓ Relational database design
- ✓ SQL DDL and DML operations
- ✓ Database normalization
- ✓ Entity-relationship modeling

### Software Engineering
- ✓ Clean code principles
- ✓ Separation of concerns
- ✓ Error handling and validation
- ✓ User interface design
- ✓ Documentation and comments

## 🚀 Future Enhancements

Potential improvements for continued learning:
- **REST API**: Convert to Spring Boot web service
- **Frontend**: Add React or Angular interface
- **Authentication**: Implement JWT tokens
- **Testing**: Add JUnit test coverage
- **Deployment**: Docker containerization
- **Monitoring**: Add logging framework

## 📞 Support

### Common Runtime Issues

**Application won't start?**
- Ensure MySQL service is running
- Verify Java 11+ is installed: `java -version`
- Check Maven installation: `mvn -version`

**Can't connect to database?**
1. Verify MySQL credentials in ConnectionManager.java
2. Ensure database exists: `SHOW DATABASES;` in MySQL
3. Check MySQL is running on port 3306

**Build errors?**
```bash
mvn clean install -U  # Force update dependencies
```

For development questions, refer to the code comments throughout the project.

## 🎯 Project Showcase

This Progress Tracker demonstrates:
- **Clean Architecture**: Separation of concerns with DAO pattern
- **Database Design**: Normalized schema with foreign key relationships
- **Error Handling**: Custom exceptions for better debugging
- **User Experience**: Intuitive console interface with clear navigation
- **Best Practices**: PreparedStatements, connection management, input validation

Perfect for demonstrating Java backend skills in interviews!
---

**Progress Tracker** - Demonstrating Java backend development skills for entry-level positions. 🚀