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
('https://images.pexels.com/photos/1108096/pexels-photo-1108096.jpeg', CURRENT_TIMESTAMP, 'Buddy', 2, 'Perro', true, 'Golden Retriever', false, 'Madrid', 'Madrid', true, 10, 1, 'Un perro amigable y juguetón en busca de un nuevo hogar.'),
('https://images.pexels.com/photos/1643454/pexels-photo-1643454.jpeg', CURRENT_TIMESTAMP, 'Mimi', 1, 'Gato', true, 'Siames', false, 'Barcelona', 'Cataluña', true, 8, 2, 'Una gata dulce y tranquila que busca un hogar amoroso.'),
('https://images.pexels.com/photos/1411453/pexels-photo-1411453.jpeg', CURRENT_TIMESTAMP, 'Rex', 3, 'Perro', true, 'Pastor Alemán', false, 'Sevilla', 'Andalucía', true, 15, 3, 'Rex es un perro leal y bien entrenado, perfecto para una familia.'),
('https://images.pexels.com/photos/210343/pexels-photo-210343.jpeg', CURRENT_TIMESTAMP, 'Kiara', 4, 'Ave', true, 'Canario', false, 'Valencia', 'Comunidad Valenciana', true, 7, 4, 'Kiara es un canario alegre que canta hermosas melodías.'),
('https://images.pexels.com/photos/5680536/pexels-photo-5680536.jpeg', CURRENT_TIMESTAMP, 'Nibbles', 1, 'Roedor', true, 'Hámster', false, 'Granada', 'Andalucía', true, 5, 5, 'Nibbles es un hámster juguetón y curioso.'),
('https://images.pexels.com/photos/1482237/pexels-photo-1482237.jpeg', CURRENT_TIMESTAMP, 'Max', 5, 'Perro', true, 'Beagle', false, 'Bilbao', 'País Vasco', true, 12, 1, 'Max es un beagle juguetón que ama las largas caminatas.'),
('https://images.pexels.com/photos/1643388/pexels-photo-1643388.jpeg', CURRENT_TIMESTAMP, 'Whiskers', 3, 'Gato', true, 'Persa', false, 'Zaragoza', 'Aragón', true, 6, 2, 'Whiskers es un gato calmado, ideal para un hogar tranquilo.'),
('https://images.pexels.com/photos/1671511/pexels-photo-1671511.jpeg', CURRENT_TIMESTAMP, 'Goldie', 2, 'Pez', true, 'Goldfish', false, 'Valladolid', 'Castilla y León', true, 9, 3, 'Goldie es un pez dorado que disfruta de su acuario.'),
('https://images.pexels.com/photos/5936447/pexels-photo-5936447.jpeg', CURRENT_TIMESTAMP, 'Rocky', 4, 'Perro', true, 'Boxer', false, 'Pamplona', 'Navarra', true, 9, 4, 'Rocky es un perro fuerte y enérgico que busca un hogar.'),
('https://images.pexels.com/photos/1616493/pexels-photo-1616493.jpeg', CURRENT_TIMESTAMP, 'Ziggy', 1, 'Reptil', true, 'Iguana', false, 'Málaga', 'Andalucía', true, 4, 5, 'Ziggy es una iguana tranquila que busca un hogar acogedor.'),
('https://images.pexels.com/photos/2202011/pexels-photo-2202011.jpeg', CURRENT_TIMESTAMP, 'Felix', 1, 'Gato', true, 'Angora', false, 'Toledo', 'Castilla-La Mancha', true, 3, 2, 'Felix es un gatito joven y lleno de energía.'),
('https://images.pexels.com/photos/1799346/pexels-photo-1799346.jpeg', CURRENT_TIMESTAMP, 'Bella', 2, 'Perro', true, 'Cocker Spaniel', false, 'Murcia', 'Murcia', true, 10, 1, 'Bella es una cocker alegre, lista para hacer parte de una familia.'),
('https://images.pexels.com/photos/2768274/pexels-photo-2768274.jpeg', CURRENT_TIMESTAMP, 'Squeaky', 2, 'Roedor', true, 'Cobaya', false, 'Almería', 'Andalucía', true, 7, 3, 'Squeaky es una cobaya adorable que ama ser acariciada.'),
('https://images.pexels.com/photos/1599462/pexels-photo-1599462.jpeg', CURRENT_TIMESTAMP, 'Nina', 3, 'Perro', true, 'Pitbull', false, 'Córdoba', 'Andalucía', true, 7, 4, 'Nina es una perra leal y protectora.'),
('https://images.pexels.com/photos/2298268/pexels-photo-2298268.jpeg', CURRENT_TIMESTAMP, 'Mia', 3, 'Ave', true, 'Agaporni', false, 'Castellón de la Plana', 'Comunidad Valenciana', true, 4, 5, 'Mia es un agaporni juguetón que disfruta de la compañía.'),
('https://images.pexels.com/photos/54351/pexels-photo-54351.jpeg', CURRENT_TIMESTAMP, 'Spike', 3, 'Exótico', true, 'Tarántula', false, 'Barcelona', 'Cataluña', true, 5, 6, 'Spike es una tarántula tranquila que necesita un hogar especial.'),
('https://images.pexels.com/photos/1742357/pexels-photo-1742357.jpeg', CURRENT_TIMESTAMP, 'Sam', 5, 'Perro', true, 'Dálmata', false, 'Almería', 'Andalucía', true, 6, 1, 'Sam es un dálmata activo que disfruta correr y jugar.');
