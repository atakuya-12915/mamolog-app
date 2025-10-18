-- ====================================
-- Heroku MySQL 8 互換 schema.sql (修正版)
-- ====================================

-- roles テーブル作成(先に作成)
CREATE TABLE IF NOT EXISTS roles (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- users テーブル作成
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,  -- BIGINT に統一
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role_id INT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- categories テーブル作成
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- todos テーブル作成 (user_id追加)
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    memo VARCHAR(255),
    account VARCHAR(50),
    category_id BIGINT NULL,
    user_id BIGINT NOT NULL,  -- ✅ 追加: User とのリレーション
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    due_date DATE NULL,
    due_time TIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE  -- ✅ 追加
);

-- diaries テーブル作成(user_id, diary_date, photo_filename追加)
CREATE TABLE IF NOT EXISTS diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    photo_filename VARCHAR(255),  -- ✅ 修正: photo_path → photo_filename
    diary_date DATE NOT NULL,     -- ✅ 追加: エンティティに存在
    user_id BIGINT NOT NULL,      -- ✅ 追加: User とのリレーション
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE  -- ✅ 追加
);