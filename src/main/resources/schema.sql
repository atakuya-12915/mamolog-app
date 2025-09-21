CREATE TABLE IF NOT EXISTS todos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,   	-- タスクID（JPAのLongに合わせてBIGINT使用）
    title VARCHAR(255) NOT NULL,                    	-- タスク名
	memo VARCHAR(255),									-- メモ（任意）
	account VARCHAR(50) NOT NULL DEFAULT 'ぱぱ',						-- アカウント切替用（ぱぱ/まま）
    completed BOOLEAN NOT NULL DEFAULT FALSE,           -- 完了フラグ
    due_date DATE NULL,									-- 日付
    due_time TIME NULL,									-- 時間
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);