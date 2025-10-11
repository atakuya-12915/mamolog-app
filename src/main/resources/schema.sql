-- ====================================
-- Heroku MySQL 8 互換 schema.sql
-- ====================================

-- categories テーブル作成
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,  -- 主キー、自動採番
    name VARCHAR(255) NOT NULL UNIQUE               -- カテゴリ名
);

-- todos テーブル作成
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,     -- 主キー、自動採番
    title VARCHAR(255) NOT NULL,                       -- タスク名
    memo VARCHAR(255),                                 -- メモ（任意）
    account VARCHAR(50),                               -- アカウント切替用（ぱぱ/まま）
    category_id BIGINT NULL,                            -- カテゴリID（外部キー）
    completed BOOLEAN NOT NULL DEFAULT FALSE,         -- 完了フラグ
    due_date DATE NULL,                                -- 期限日
    due_time TIME NULL,                                -- 期限時間
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT uq_todo UNIQUE (due_date, due_time)     -- 同じ日の同じ時刻の重複禁止
);

-- diaries テーブル作成
CREATE TABLE IF NOT EXISTS diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,             -- 主キー
    comment TEXT NOT NULL,                             -- コメント本文
    photo_path VARCHAR(255),                           -- 写真ファイルパス
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- roles テーブル作成
CREATE TABLE IF NOT EXISTS roles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- users テーブル作成
CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role_id INT NOT NULL, 
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
