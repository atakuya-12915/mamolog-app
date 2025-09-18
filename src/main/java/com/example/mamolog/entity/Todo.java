package com.example.mamolog.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "todos")
@Data
public class Todo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String title;		// タスクのタイトル（必須）
	
	private String memo;		// タスクのメモ（任意）

	@Column(nullable = false)
	private String account;		// 担当者（ぱぱ or まま）

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")	// 外部キー名（todosテーブルにcategory_idカラムが作成）
	@ToString.Exclude			// Entityの双方向参照を無効化のため
	@EqualsAndHashCode.Exclude	// Entityの双方向参照を無効化のため
	private Category category;
	
	@Column(name = "due_date_time", nullable = false)
	private LocalDate dueDateTime; 		// 期限日時（＝「今日のやること」の基準）
	
	@Column(nullable = false)
	private boolean completed = false;		// 完了フラグ（初期値はfalse）
}
