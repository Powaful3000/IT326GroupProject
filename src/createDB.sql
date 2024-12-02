-- Drop the existing database if it exists
DROP DATABASE IF EXISTS StudentDB;

-- Create a new database
CREATE DATABASE StudentDB;

-- Use the new database
USE StudentDB;

-- Create the students table
CREATE TABLE students (
    userID INT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL, -- SHA-256 hash is 64 characters
    userYear VARCHAR(50)
);

-- Create the student_groups table
CREATE TABLE student_groups (
    groupID INT PRIMARY KEY,
    groupName VARCHAR(255) NOT NULL UNIQUE,
    groupSize INT,
    groupDescription TEXT
);

-- Create the group_memberships table
CREATE TABLE group_memberships (
    groupID INT,
    studentID INT,
    joinDate TIMESTAMP,
    PRIMARY KEY (groupID, studentID),
    FOREIGN KEY (groupID) REFERENCES student_groups(groupID),
    FOREIGN KEY (studentID) REFERENCES students(userID)
);

-- Create the posts table
CREATE TABLE posts (
    postID INT PRIMARY KEY,
    postContent TEXT,
    postOwner INT,
    FOREIGN KEY (postOwner) REFERENCES students(userID)
);

-- Insert a test user with a SHA-256 hashed password
INSERT INTO students (userID, userName, password, userYear) VALUES
(1001, 'testuser1', SHA2('testuser1', 256), 'Freshman');