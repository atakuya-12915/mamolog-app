package com.example.mamolog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "todos")
@Data
public class Todo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;						// 自動採番ID
	
	@Column(nullable = false)
	private String title;					// タスク名
	
	private String memo;					// タスクのメモ（任意）
	private String account;					// 担当者（ぱぱ／まま）

	@Column(nullable = false)
	private boolean completed = false;		// 完了フラグ（初期値はfalse）
	
	private LocalDate dueDate; 				// 期限日
    private LocalTime dueTime; 				// 期限時刻
	
	// 作成日時
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	// 更新日時
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	// 保存時の自動設定（JPA/Hibernate でエンティティの作成・更新時に自動で日時をセットする処理/Java側で制御）
	@PrePersist									// 新規作成時の処理
    protected void onCreate() {					// エンティティが最初にDBに保存される前に呼ばれるメソッド
        this.createdAt = LocalDateTime.now();	// 作成日時をセット
        this.updatedAt = createdAt;				// 作成日時と同じ値で初期化
    }

    @PreUpdate									// 更新時の処理
    protected void onUpdate() {					// エンティティが 更新される直前 に呼ばれるメソッド
        this.updatedAt = LocalDateTime.now();	// 現在日時をセット
    }
}
