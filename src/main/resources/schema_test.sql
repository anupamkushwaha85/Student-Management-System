-- This schema is a simplified version for the H2 in-memory test database.

CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    address VARCHAR(255),
    department VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_department (department),
    INDEX idx_status (status)
);

-- Set the initial ID for consistency with the main application logic
ALTER TABLE students AUTO_INCREMENT = 101;