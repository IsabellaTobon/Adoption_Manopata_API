-- Insert roles if doesn't exist
INSERT IGNORE INTO roles (name, description)
VALUES
('USER', 'Regular user with limited permissions'),
('ADMIN', 'Administrator with full access'),
('PROTECTOR', 'Protector of animals with access to manage animal posts');