-- ====================================
-- data.sql (初期データ)
-- ====================================

-- roles の初期データ
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER') ON DUPLICATE KEY UPDATE name=name;

-- categories の初期データ
INSERT INTO categories (id, name) VALUES (1, '家事') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (id, name) VALUES (2, '育児') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (id, name) VALUES (3, '買い物') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (id, name) VALUES (4, '仕事') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (id, name) VALUES (5, 'その他') ON DUPLICATE KEY UPDATE name=name;

-- テスト用ユーザー (パスワード: password)
INSERT INTO users (id, email, password, name, role_id, enabled) 
VALUES (1, 'papa@mamolog.com', '$2a$10$ZtIGeZUWyM3KQYxi0zGChOaPSOXdsVFSBtw9942wLkqayEZAitEKi', 'ぱぱ', 2, TRUE)
ON DUPLICATE KEY UPDATE email=email;

-- テスト用ユーザー2 (パスワード: password)
INSERT INTO users (id, email, password, name, role_id, enabled) 
VALUES (2, 'mama@mamolog.com', '$2a$10$ZtIGeZUWyM3KQYxi0zGChOaPSOXdsVFSBtw9942wLkqayEZAitEKi', 'まま', 2, TRUE)
ON DUPLICATE KEY UPDATE email=email;