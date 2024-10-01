-- Insertar 5 usuarios si no existen
INSERT IGNORE INTO users (name, lastname, nickname, email, password, role, active, deleted) VALUES
('Isabel', 'Torres', 'IsabelT', 'isabel.torres@example.com', 'password1', 'USER', true, false),
('Carlos', 'Fernandez', 'CarlosF', 'carlos.fernandez@example.com', 'password2', 'USER', true, false),
('Lucia', 'Gomez', 'LuciaG', 'lucia.gomez@example.com', 'password3', 'USER', true, false),
('Maria', 'Lopez', 'MariaL', 'maria.lopez@example.com', 'password4', 'USER', true, false),
('Juan', 'Martinez', 'JuanM', 'juan.martinez@example.com', 'password5', 'USER', true, false);

-- Insertar 4 comentarios si no existen
INSERT IGNORE INTO comments (name, text, rating, userId) VALUES
('IsabelT', 'Este sitio es increíble, adopté mi mascota y estoy muy contenta', 5, 1),
('CarlosF', 'Muy buen servicio, pero podrían mejorar el diseño', 4, 2),
('LuciaG', 'Tuve un problema con un anuncio, pero fue resuelto rápidamente', 4, 3),
('MariaL', 'Excelente lugar para encontrar una nueva mascota', 5, 4);

-- Insertar 18 posts si no existen, usando URLs de imágenes libres
INSERT IGNORE INTO posts (photo, registerDate, name, age, animalType, vaccinated, breed, ppp, city, province, available, likes, userId, description) VALUES
('https://source.unsplash.com/random/200x200?dog1', CURRENT_TIMESTAMP, 'Buddy', 2, 'Perro', true, 'Golden Retriever', false, 'Madrid', 'Madrid', true, 10, 1, 'Un perro amigable y juguetón en busca de un nuevo hogar.'),
('https://source.unsplash.com/random/200x200?cat1', CURRENT_TIMESTAMP, 'Mimi', 1, 'Gato', true, 'Siames', false, 'Barcelona', 'Cataluña', true, 8, 2, 'Una gata dulce y tranquila que busca un hogar amoroso.'),
('https://source.unsplash.com/random/200x200?dog2', CURRENT_TIMESTAMP, 'Rex', 3, 'Perro', true, 'Pastor Alemán', false, 'Sevilla', 'Andalucía', true, 15, 3, 'Rex es un perro leal y bien entrenado, perfecto para una familia.'),
('https://source.unsplash.com/random/200x200?dog3', CURRENT_TIMESTAMP, 'Luna', 4, 'Perro', true, 'Labrador', false, 'Valencia', 'Comunidad Valenciana', true, 7, 4, 'Luna es una perrita cariñosa y muy activa.'),
('https://source.unsplash.com/random/200x200?cat2', CURRENT_TIMESTAMP, 'Simba', 2, 'Gato', true, 'Maine Coon', false, 'Granada', 'Andalucía', true, 5, 5, 'Simba es un gato curioso y muy independiente.'),
('https://source.unsplash.com/random/200x200?dog4', CURRENT_TIMESTAMP, 'Max', 5, 'Perro', true, 'Beagle', false, 'Bilbao', 'País Vasco', true, 12, 1, 'Max es un beagle juguetón que ama las largas caminatas.'),
('https://source.unsplash.com/random/200x200?cat3', CURRENT_TIMESTAMP, 'Whiskers', 3, 'Gato', true, 'Persa', false, 'Zaragoza', 'Aragón', true, 6, 2, 'Whiskers es un gato calmado, ideal para un hogar tranquilo.'),
('https://source.unsplash.com/random/200x200?dog5', CURRENT_TIMESTAMP, 'Bruno', 6, 'Perro', true, 'Bulldog', false, 'Valladolid', 'Castilla y León', true, 9, 3, 'Bruno es un bulldog tierno que necesita un hogar con mucho amor.'),
('https://source.unsplash.com/random/200x200?dog6', CURRENT_TIMESTAMP, 'Toby', 1, 'Perro', true, 'Chihuahua', false, 'Santander', 'Cantabria', true, 4, 4, 'Toby es un perrito pequeño pero con un gran corazón.'),
('https://source.unsplash.com/random/200x200?cat4', CURRENT_TIMESTAMP, 'Cleo', 4, 'Gato', true, 'Bengalí', false, 'Alicante', 'Comunidad Valenciana', true, 11, 5, 'Cleo es una gata juguetona y cariñosa.'),
('https://source.unsplash.com/random/200x200?dog7', CURRENT_TIMESTAMP, 'Bella', 2, 'Perro', true, 'Cocker Spaniel', false, 'Murcia', 'Murcia', true, 10, 1, 'Bella es una cocker alegre, lista para hacer parte de una familia.'),
('https://source.unsplash.com/random/200x200?cat5', CURRENT_TIMESTAMP, 'Felix', 1, 'Gato', true, 'Angora', false, 'Toledo', 'Castilla-La Mancha', true, 3, 2, 'Felix es un gatito joven y lleno de energía.'),
('https://source.unsplash.com/random/200x200?dog8', CURRENT_TIMESTAMP, 'Rocky', 4, 'Perro', true, 'Boxer', false, 'Pamplona', 'Navarra', true, 9, 3, 'Rocky es un perro fuerte y enérgico que busca un hogar.'),
('https://source.unsplash.com/random/200x200?dog9', CURRENT_TIMESTAMP, 'Nina', 3, 'Perro', true, 'Pitbull', false, 'Córdoba', 'Andalucía', true, 7, 4, 'Nina es una perra leal y protectora.'),
('https://source.unsplash.com/random/200x200?cat6', CURRENT_TIMESTAMP, 'Lily', 3, 'Gato', true, 'Birmano', false, 'Málaga', 'Andalucía', true, 4, 5, 'Lily es una gata tranquila que necesita un hogar cómodo.'),
('https://source.unsplash.com/random/200x200?dog10', CURRENT_TIMESTAMP, 'Sam', 5, 'Perro', true, 'Dálmata', false, 'Almería', 'Andalucía', true, 6, 1, 'Sam es un dálmata activo que disfruta correr y jugar.'),
('https://source.unsplash.com/random/200x200?cat7', CURRENT_TIMESTAMP, 'Oscar', 4, 'Gato', true, 'Azul Ruso', false, 'A Coruña', 'Galicia', true, 5, 2, 'Oscar es un gato curioso y muy independiente.');

