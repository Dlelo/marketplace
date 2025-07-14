INSERT INTO role (name, description) VALUES ('HOMEOWNER', 'Houseowner role');
INSERT INTO role (name, description) VALUES ('HOUSEHELP', 'Househelp role');
INSERT INTO role (name, description) VALUES ('ADMIN', 'Administrator role');
INSERT INTO user (username, email, password) VALUES ('admin', 'admin@example.com', '$2a$10$YOUR_BCRYPT_HASHED_PASSWORD'); -- Replace with BCrypt hash
INSERT INTO user_roles (user_id, role_id) VALUES ((SELECT id FROM user WHERE email = 'admin@example.com'), (SELECT id FROM role WHERE name = 'ADMIN'));