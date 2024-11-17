INSERT INTO roles (id, name) VALUES ('50cde66f-cb0c-4458-a6c9-e3cc4a4068ee', 'ROLE_USER') ON CONFLICT DO NOTHING;
INSERT INTO roles (id, name) VALUES ('f732ea1f-7eb4-4102-93ee-ca4fd3a26116', 'ROLE_ADMIN') ON CONFLICT DO NOTHING;

-- Hardcoded Admin User (admin@ligma.ltd:admin)

INSERT INTO users (id, created_at, updated_at, email, first_name, last_name, middle_name, password) VALUES ('e8da7bc9-aea2-475d-b6af-edeca82a40d0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin@ligma.ltd', 'Иванов', 'Иван', 'Иванович', '$2y$10$6xRWt3wGyGT/ZQYtwYwbsusi8UPf5xxi5Pkuca.BgHMdUO6hKgDxq') ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id) VALUES ('e8da7bc9-aea2-475d-b6af-edeca82a40d0', '50cde66f-cb0c-4458-a6c9-e3cc4a4068ee'), ('e8da7bc9-aea2-475d-b6af-edeca82a40d0', 'f732ea1f-7eb4-4102-93ee-ca4fd3a26116') ON CONFLICT DO NOTHING;
