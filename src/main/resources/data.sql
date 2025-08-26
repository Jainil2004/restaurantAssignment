TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE restaurants RESTART IDENTITY CASCADE;
TRUNCATE TABLE menu_item RESTART IDENTITY CASCADE;

-- USERS
INSERT INTO users (id, name, password, role, country)
VALUES
    (1, 'NickFury',  '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'ADMIN',   'INDIA'),
    (2, 'CaptainMarvel',  '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'MANAGER', 'INDIA'),
    (3, 'CaptainAmerica', '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'MANAGER', 'AMERICA'),
    (4, 'Thanos',         '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'MEMBER',  'INDIA'),
    (5, 'Thor',           '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'MEMBER',  'INDIA'),
    (6, 'Travis',         '$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW', 'MEMBER',  'AMERICA');

-- RESTAURANTS
INSERT INTO restaurants (id, name, country)
VALUES
    (1, 'Delhi Dhaba', 'INDIA'),
    (2, 'New York Grill', 'AMERICA');

-- MENU ITEMS
INSERT INTO menu_item (id, name, price, restaurant_id)
VALUES
    (1, 'Paneer Butter Masala', 250.00, 1),
    (2, 'Chicken Biryani',      300.00, 1),
    (3, 'Cheeseburger',          10.00, 2),
    (4, 'BBQ Ribs',              20.00, 2);
