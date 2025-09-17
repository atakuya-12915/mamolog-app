CREATE TABLE IF NOT EXISTS todos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,       -- タスクID（JPAのLongに合わせてBIGINT推奨）
    title VARCHAR(255) NOT NULL,                         -- タスク名
	memo TEXT,											-- メモ
    account VARCHAR(50),                                  -- 担当者（夫 or 妻）
    category_id BIGINT,									-- 外部キー
    completed BOOLEAN NOT NULL DEFAULT FALSE,           -- 完了フラグ
    due_date_time DATETIME,								-- 今日の日付
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,      -- カテゴリID
    name VARCHAR(255) NOT NULL
);
