-- -----------------------------------------------------
-- Student Management System Schema
-- Author: Anupam Kushwaha
-- -----------------------------------------------------

-- This script sets up the database and table for the Student Management System.
-- Version 2: Includes user-suggested improvements for data integrity, auditing, and performance.

-- Drop the database if it exists to ensure a clean setup.
DROP DATABASE IF EXISTS student_system;

-- Create the new database.
CREATE DATABASE student_system;

-- Switch to the newly created database.
USE student_system;

-- Create the main table to store student records.
CREATE TABLE students (
    -- Core Student Information
    id INT PRIMARY KEY AUTO_INCREMENT,                      -- Unique ID for each student, handled by the database.
    name VARCHAR(100) NOT NULL,                             -- Student's full name, required.
    email VARCHAR(100) NOT NULL UNIQUE,                     -- Student's email, required and must be unique.
    phone VARCHAR(20) NOT NULL,                             -- Student's phone number, required.
    dob DATE NOT NULL,                                      -- Student's Date of Birth, required.
    address VARCHAR(255),                                   -- Student's physical address, optional.

    -- Academic Information
    department VARCHAR(50) NOT NULL,                        -- The student's department (e.g., 'CSE'), required.
    status VARCHAR(50) NOT NULL,                            -- The student's current status (e.g., 'ACTIVE'), required.

    -- Auditing Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Automatically sets the creation time.
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Automatically updates when the record is changed.

    -- Performance Indexes
    INDEX idx_name (name),                                  -- An index to speed up searching by student name.
    INDEX idx_department (department),                      -- An index to speed up filtering by department.
    INDEX idx_status (status)                               -- An index to speed up filtering by status.
);

-- for id starting with 101 (optional)
-- ALTER TABLE students AUTO_INCREMENT = 101;