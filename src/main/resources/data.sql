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

INSERT INTO users (id, email, password, name, role_id, enabled) 
VALUES (2, 'mama@mamolog.com', '$2a$10$ZtIGeZUWyM3KQYxi0zGChOaPSOXdsVFSBtw9942wLkqayEZAitEKi', 'まま', 2, TRUE)
ON DUPLICATE KEY UPDATE email=email;

-- ====================================
-- Todo データ (11件)
-- ====================================
INSERT INTO todos (id, title, description, category_id, user_id, completed) VALUES
(1, '朝食準備', '子供用の離乳食と牛乳を用意', 1, 2, FALSE),
(2, 'オムツ交換', '朝のオムツ交換', 2, 2, FALSE),
(3, '保育園の送迎', 'ままが子供を保育園に送る', 2, 2, FALSE),
(4, '夕食の買い物', 'スーパーで野菜と肉を購入', 3, 1, FALSE),
(5, '洗濯', '子供の服とタオルを洗濯', 1, 2, FALSE),
(6, 'パパの会議資料作成', '明日の会議資料をまとめる', 4, 1, FALSE),
(7, 'お風呂準備', 'お湯の温度チェックとおもちゃ用意', 2, 2, FALSE),
(8, 'ゴミ出し', '燃えるゴミをまとめる', 1, 1, FALSE),
(9, 'おやつ作り', 'クッキーとフルーツを準備', 1, 2, FALSE),
(10, '買い物リスト作成', '必要な日用品をリストアップ', 3, 2, FALSE),
(11, '洗い物', '食器洗いと片付け', 1, 2, FALSE);

-- ====================================
-- 日記データ (3件)
-- ====================================
INSERT INTO diaries (id, content, user_id) VALUES
(1, '夕食は子供の好きなハンバーグを作った', 2),
(2, 'まま、近所の公園まで散歩した', 2),
(3, 'ぱぱ、子供と一緒に絵本を読んだ', 1);
