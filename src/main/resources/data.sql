-- カテゴリサンプル（重複があれば更新せず無視）
INSERT INTO categories (name) VALUES
('家事'),
('買い物'),
('育児')
ON DUPLICATE KEY UPDATE name = name;

-- Todoのサンプルデータ
INSERT INTO todos (title, memo, account, completed, created_at, updated_at, category_id, due_date, due_time)
VALUES ('掃除', 'リビング', 'ぱぱ', 0, NOW(), NOW(), 1, '2025-09-24', '09:00:00');

INSERT INTO todos (title, memo, account, completed, created_at, updated_at, category_id, due_date, due_time)
VALUES ('洗濯', '衣類の洗濯', 'ぱぱ', 0, NOW(), NOW(), 1, '2025-09-24', '10:00:00');

INSERT INTO todos (title, memo, account, completed, created_at, updated_at, category_id, due_date, due_time)
VALUES ('買い物', '牛乳と卵', 'まま', 0, NOW(), NOW(), 2, '2025-09-24', '15:00:00');

-- ▼ 同じ日・同じタイトルだが時間違い → 登録可能
INSERT INTO todos (title, memo, account, completed, created_at, updated_at, category_id, due_date, due_time)
VALUES ('ランニング', '朝の運動', 'ぱぱ', 0, NOW(), NOW(), NULL, '2025-09-24', '07:00:00');

INSERT INTO todos (title, memo, account, completed, created_at, updated_at, category_id, due_date, due_time)
VALUES ('ランニング', '夜の運動', 'ぱぱ', 0, NOW(), NOW(), NULL, '2025-09-24', '19:00:00');