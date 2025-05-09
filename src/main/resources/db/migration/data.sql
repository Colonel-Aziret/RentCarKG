-- Insert initial data into the 'users' table
INSERT INTO users (email, password, role)
VALUES ('client@example.com', 'qweasdzxc', 'CLIENT'), -- A client user
       ('owner@example.com', 'qweasdzxc', 'OWNER'),   -- An owner user
       ('admin@example.com', 'qweasdzxc', 'ADMIN');
-- An admin user

-- Insert initial data into the 'cars' table
INSERT INTO cars (brand, model, price_per_day, owner_id, year, color, description, image_url, capacity, fuel_type,
                  transmission)
VALUES ('Toyota', 'Camry', 50.00, 2, 2020, 'Black', 'Spacious family sedan', 'https://example.com/toyota-camry.jpg', 5,
        'Petrol', 'Automatic'), -- A Toyota Camry
       ('Honda', 'Civic', 40.00, 2, 2019, 'Blue', 'Compact car for city driving', 'https://example.com/honda-civic.jpg',
        5, 'Petrol', 'Manual'), -- A Honda Civic
       ('BMW', 'X5', 120.00, 3, 2022, 'White', 'Luxury SUV', 'https://example.com/bmw-x5.jpg', 7, 'Diesel',
        'Automatic');
-- A BMW X5

-- Insert initial data into the 'bookings' table
INSERT INTO bookings (start_date, end_date, status, total_price, car_id, user_id, created_at, penalty)
VALUES ('2025-05-01', '2025-05-07', 'PENDING', 350.00, 1, 1, '2025-04-01 10:00:00', 0.00),    -- A pending booking
       ('2025-05-10', '2025-05-12', 'CONFIRMED', 120.00, 2, 2, '2025-04-02 14:30:00', 10.00), -- A confirmed booking
       ('2025-06-01', '2025-06-05', 'CANCELED', 0.00, 3, 1, '2025-04-03 09:15:00', 20.00);
-- A canceled booking

-- Insert initial data into the 'locations' table
INSERT INTO locations (city, address)
VALUES ('Бишкек', 'проспект Чуй 137'),
       ('Ош', 'улица Ленина 25'),
       ('Каракол', 'улица Абдрахманова 12'),
       ('Чолпон-Ата', 'курортная зона Иссык-Куль'),
       ('Талас', 'улица Манаса 10'),
       ('Нарын', 'улица Сыдыкова 7'),
       ('Токмок', 'улица Фрунзе 8');
