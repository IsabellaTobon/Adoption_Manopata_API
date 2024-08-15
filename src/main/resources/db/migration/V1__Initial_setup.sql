-- Crear tabla roles si no existe
CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(20) PRIMARY KEY NOT NULL,
    description VARCHAR(100) NOT NULL
);

-- Crear tabla users si no existe
CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    nickname VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    FOREIGN KEY (role) REFERENCES roles(name)
);

-- Crear tabla posts si no existe
CREATE TABLE IF NOT EXISTS posts (
    id CHAR(36) PRIMARY KEY NOT NULL,
    photo TEXT NOT NULL,
    registerDate DATE NOT NULL,
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
    userId CHAR(36) NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id)
);

-- Crear tabla messages si no existe
CREATE TABLE IF NOT EXISTS messages (
    id CHAR(36) PRIMARY KEY NOT NULL,
    bodyText TEXT NOT NULL,
    sentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    senderId CHAR(36) NOT NULL,
    receiverId CHAR(36) NOT NULL,
    postId CHAR(36) NOT NULL,
    FOREIGN KEY (senderId) REFERENCES users(id),
    FOREIGN KEY (receiverId) REFERENCES users(id),
    FOREIGN KEY (postId) REFERENCES posts(id)
);

-- Crear tabla protectors si no existe
CREATE TABLE IF NOT EXISTS protectors (
    id CHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    phone VARCHAR(13) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
