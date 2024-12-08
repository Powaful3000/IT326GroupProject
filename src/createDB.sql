-- =============================================
-- Database Cleanup
-- =============================================
-- Drop the entire database if it exists to start fresh
DROP DATABASE IF EXISTS StudentDB;

-- Create a new empty database
CREATE DATABASE StudentDB;

-- Switch to the newly created database
USE StudentDB;

-- =============================================
-- Drop All Existing Tables
-- =============================================
-- Drop order is important due to foreign key relationships
-- First drop all junction/relationship tables
DROP TABLE IF EXISTS bookmarked_posts;
DROP TABLE IF EXISTS blocked_users;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS post_groups;
DROP TABLE IF EXISTS student_tags;
DROP TABLE IF EXISTS group_memberships;

-- Then drop independent tables (those without foreign keys)
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS student_groups;
DROP TABLE IF EXISTS students;

-- =============================================
-- Create Core Tables
-- =============================================
-- Create the main students table - this is the core entity
CREATE TABLE
    students (
        userID INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier for each student
        userName VARCHAR(255) NOT NULL, -- Username must be unique
        password VARCHAR(255) NOT NULL, -- SHA-256 hash is 64 characters
        userYear VARCHAR(20) NOT NULL, -- Student's academic year
        isAnonymous BOOLEAN DEFAULT FALSE -- Whether student is in anonymous mode
    );

-- Create the groups table - for student groups/clubs
CREATE TABLE
    student_groups (
        groupID INT PRIMARY KEY, -- Unique identifier for each group
        groupName VARCHAR(100) NOT NULL, -- Name of the group
        groupSize INT DEFAULT 0, -- Number of members (maintained by triggers/updates)
        groupDescription TEXT, -- Detailed description of the group
        creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- When the group was created
    );

-- Create the posts table - for student posts/messages
CREATE TABLE
    posts (
        postID INT PRIMARY KEY, -- Unique identifier for each post
        postContent TEXT NOT NULL, -- The actual content of the post
        postOwner INT, -- Reference to the student who created it
        postDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- When the post was created
        FOREIGN KEY (postOwner) REFERENCES students (userID) ON DELETE CASCADE -- Posts are deleted when their owner is deleted
    );

-- Create the tags table - for categorizing students/content
CREATE TABLE
    tags (
        tagID INT PRIMARY KEY AUTO_INCREMENT, -- Auto-incrementing tag identifier
        name VARCHAR(50) NOT NULL UNIQUE, -- Tag name must be unique
        description TEXT -- Optional description of what the tag means
    );

-- =============================================
-- Create Junction/Relationship Tables
-- =============================================
-- Table for group membership tracking
CREATE TABLE
    group_memberships (
        groupID INT, -- Reference to the group
        studentID INT, -- Reference to the student member
        joinDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- When they joined
        endDate DATE, -- When they left (NULL if still active)
        PRIMARY KEY (groupID, studentID), -- Composite key of both IDs
        FOREIGN KEY (groupID) REFERENCES student_groups (groupID) ON DELETE CASCADE,
        FOREIGN KEY (studentID) REFERENCES students (userID) ON DELETE CASCADE
    );

-- Table linking students to their tags
CREATE TABLE
    student_tags (
        studentID INT, -- Reference to the student
        tagID INT, -- Reference to the tag
        PRIMARY KEY (studentID, tagID), -- Composite key of both IDs
        FOREIGN KEY (studentID) REFERENCES students (userID) ON DELETE CASCADE,
        FOREIGN KEY (tagID) REFERENCES tags (tagID) ON DELETE CASCADE
    );

-- Table linking posts to groups they belong to
CREATE TABLE
    post_groups (
        postID INT, -- Reference to the post
        groupID INT, -- Reference to the group
        PRIMARY KEY (postID, groupID), -- Composite key of both IDs
        FOREIGN KEY (postID) REFERENCES posts (postID) ON DELETE CASCADE,
        FOREIGN KEY (groupID) REFERENCES student_groups (groupID) ON DELETE CASCADE
    );

-- =============================================
-- Create Social Features Tables
-- =============================================
-- Table for tracking friend requests
CREATE TABLE
    friend_requests (
        requestID INT PRIMARY KEY AUTO_INCREMENT, -- Unique identifier for each request
        fromUserID INT, -- Student sending the request
        toUserID INT, -- Student receiving the request
        requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When request was sent
        status ENUM ('PENDING', 'ACCEPTED', 'DECLINED') DEFAULT 'PENDING', -- Request status
        FOREIGN KEY (fromUserID) REFERENCES students (userID) ON DELETE CASCADE, -- Remove requests when sender is deleted
        FOREIGN KEY (toUserID) REFERENCES students (userID) ON DELETE CASCADE    -- Remove requests when receiver is deleted
    );

-- Table for tracking confirmed friendships
CREATE TABLE
    friends (
        userID1 INT, -- First student in friendship
        userID2 INT, -- Second student in friendship
        friendshipDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When they became friends
        PRIMARY KEY (userID1, userID2), -- Composite key of both IDs
        FOREIGN KEY (userID1) REFERENCES students (userID) ON DELETE CASCADE,
        FOREIGN KEY (userID2) REFERENCES students (userID) ON DELETE CASCADE
    );

-- Table for tracking blocked users
CREATE TABLE
    blocked_users (
        blockerID INT, -- Student doing the blocking
        blockedID INT, -- Student being blocked
        blockDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When the block occurred
        PRIMARY KEY (blockerID, blockedID), -- Composite key of both IDs
        FOREIGN KEY (blockerID) REFERENCES students (userID) ON DELETE CASCADE,
        FOREIGN KEY (blockedID) REFERENCES students (userID) ON DELETE CASCADE
    );

-- Table for tracking bookmarked posts
CREATE TABLE
    bookmarked_posts (
        userID INT, -- Student who bookmarked
        postID INT, -- Post that was bookmarked
        bookmarkDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When it was bookmarked
        PRIMARY KEY (userID, postID), -- Composite key of both IDs
        FOREIGN KEY (userID) REFERENCES students (userID) ON DELETE CASCADE,
        FOREIGN KEY (postID) REFERENCES posts (postID) ON DELETE CASCADE
    );

-- =============================================
-- Insert Initial Test Data
-- =============================================
-- Create test users with hashed passwords
INSERT INTO
    students (userID, userName, password, userYear)
VALUES
    (
        1001,
        'testuser1',
        SHA2('testuser1', 256),
        'Senior'
    ),
    (
        1002,
        'testuser2',
        SHA2('testuser2', 256),
        'Junior'
    ),
    (
        1003,
        'dsadsa321',
        SHA2('dsadsa321', 256),
        'Sophomore'
    );

-- Password is hashed using SHA-256
-- Insert a test group
INSERT INTO
    student_groups (
        groupID,
        groupName,
        groupDescription,
        groupSize,
        creationDate
    )
VALUES
    (1, 'tg1', 'td1', 0, CURRENT_TIMESTAMP);

-- Insert another test group
INSERT INTO
    student_groups (
        groupID,
        groupName,
        groupDescription,
        groupSize,
        creationDate
    )
VALUES
    (2, 'tg2', 'td2', 0, CURRENT_TIMESTAMP);