-- categories テーブル作成 --
CREATE TABLE IF NOT EXISTS categories (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,			-- 主キー、自動採番（外部キー）
	name VARCHAR(255) NOT NULL UNIQUE						-- カテゴリ名
);

-- todos テーブル作成 --
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,   		-- 主キー、自動採番（JPAのLongに合わせてBIGINT使用）
    title VARCHAR(255) NOT NULL,                    		-- タスク名
	memo VARCHAR(255),										-- メモ（任意）
	account VARCHAR(50),									-- アカウント切替用（ぱぱ/まま）
	category_id BIGINT NULL,								-- カテゴリID（外部キー制約なし）
    completed BOOLEAN NOT NULL DEFAULT FALSE,           	-- 完了フラグ
    due_date DATE NULL,										-- 期限日
    due_time TIME NULL,										-- 期限時間
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),	-- タスクの一意性をidで管理
    -- ▼ 同じ日の同じ時刻・同じタイトルでの重複を禁止する制約
	CONSTRAINT uq_todo UNIQUE (due_date, due_time)	-- 例）07:00と19:00なら登録OK
);

-- diariesテーブル作成
CREATE TABLE diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  					-- 主キー（必須）
    comment TEXT NOT NULL,                 					-- コメント本文（複数行）
    photo_path VARCHAR(255),               					-- 写真ファイルのパス（1枚）
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, 								-- 作成日時
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 	-- 更新日時
);


-- 外部キー制約（todos.category_id に入る値が、必ず categories.id に存在する値に限定する） --
-- ALTER TABLE todos
-- ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id);
