-- Drop existing database and all its tables (handles dropping in correct order due to CASCADE)
DROP DATABASE IF EXISTS StudentDB;

-- Create fresh database
CREATE DATABASE StudentDB;

USE StudentDB;

-- Create students table (core entity)
CREATE TABLE
    students (
        userID INT PRIMARY KEY,
        userName VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(64) NOT NULL, -- SHA-256 hash is 64 characters
        userYear VARCHAR(20) NOT NULL
    );

-- Create student_groups table (core entity)
CREATE TABLE
    student_groups (
        groupID INT PRIMARY KEY,
        groupName VARCHAR(100) NOT NULL,
        groupSize INT DEFAULT 0,
        groupDescription TEXT
    );

-- Create posts table (depends on students)
CREATE TABLE
    posts (
        postID INT PRIMARY KEY,
        postContent TEXT NOT NULL,
        postOwner INT,
        postDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (postOwner) REFERENCES students (userID) ON DELETE CASCADE
    );

-- Create tags table (independent entity)
CREATE TABLE
    tags (
        tagID INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL UNIQUE,
        description TEXT
    );

-- Create junction table for students and groups (membership)
CREATE TABLE
    group_memberships (
        groupID INT,
        studentID INT,
        joinDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        endDate DATE,
        PRIMARY KEY (groupID, studentID),
        FOREIGN KEY (groupID) REFERENCES student_groups (groupID) ON DELETE CASCADE,
        FOREIGN KEY (studentID) REFERENCES students (userID) ON DELETE CASCADE
    );

-- Create junction table for students and tags
CREATE TABLE
    student_tags (
        studentID INT,
        tagID INT,
        PRIMARY KEY (studentID, tagID),
        FOREIGN KEY (studentID) REFERENCES students (userID) ON DELETE CASCADE,
        FOREIGN KEY (tagID) REFERENCES tags (tagID) ON DELETE CASCADE
    );

-- Create junction table for posts and groups
CREATE TABLE
    post_groups (
        postID INT,
        groupID INT,
        PRIMARY KEY (postID, groupID),
        FOREIGN KEY (postID) REFERENCES posts (postID) ON DELETE CASCADE,
        FOREIGN KEY (groupID) REFERENCES student_groups (groupID) ON DELETE CASCADE
    );

-- Insert a test user with a SHA-256 hashed password
INSERT INTO
    students (userID, userName, password, userYear)
VALUES
    (
        1001,
        'testuser1',
        SHA2 ('testuser1', 256),
        'Senior'
    );