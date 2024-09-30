-- Create roles table if doesn't exist
CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(20) PRIMARY KEY NOT NULL,
    description VARCHAR(100) NOT NULL
);

-- Create users table if doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    nickname VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (role) REFERENCES roles(name)
);

-- Create protectors table if doesn't exist
CREATE TABLE IF NOT EXISTS protectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    phone VARCHAR(13) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    city VARCHAR(50) NOT NULL,
    province VARCHAR(50),
    photo VARCHAR(255),
    web_site VARCHAR(255)
);

-- Create posts table if doesn't exist
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    photo TEXT NOT NULL,
    registerDate TIMESTAMP NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    animalType VARCHAR(30) NOT NULL,
    vaccinated BOOLEAN NOT NULL,
    breed VARCHAR(50),
    ppp BOOLEAN,
    city VARCHAR(50),
    province VARCHAR(50) NOT NULL,
    available BOOLEAN,
    likes INT DEFAULT 0,
    userId BIGINT NOT NULL,
    protector_id BIGINT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (protector_id) REFERENCES protectors(id)
);

-- Create messages table if doesn't exist
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    bodyText TEXT NOT NULL,
    sentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    senderId BIGINT NOT NULL,
    receiverId BIGINT NOT NULL,
    postId BIGINT NOT NULL,
    FOREIGN KEY (senderId) REFERENCES users(id),
    FOREIGN KEY (receiverId) REFERENCES users(id),
    FOREIGN KEY (postId) REFERENCES posts(id)
);

-- Create comments table if doesn't exist
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    text TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    userId BIGINT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id)
);

