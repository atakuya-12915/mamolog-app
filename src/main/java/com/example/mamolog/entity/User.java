package com.example.mamolog.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;		// メールアドレス

	@Column(nullable = false)
	private String password; 	// ハッシュ化

	private String name; 	// 表示名（例：ぱぱ、まま）
	
	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;			// ロール情報
	
	@Column(nullable = false)
	private boolean enabled = true;	// アカウントの有効/無効（デフォルトは有効）

	// Todo との双方向リレーション
	@OneToMany(mappedBy = "user", 
			   cascade = CascadeType.ALL,		// 親：User に対する操作（保存・削除など）を 子：Todo に伝播
			   orphanRemoval = true				// 親：User から 子：Todo をリストから削除すると、DB からも自動で削除
			   )
	private List<Todo> todos = new ArrayList<>();

	// Diary との双方向リレーション
	@OneToMany(mappedBy = "user",
			   cascade = CascadeType.ALL,		// 親：User に対する操作（保存・削除など）を 子：Diary に伝播
			   orphanRemoval = true				// 親：User から 子：Diary をリストから削除すると、DB からも自動で削除
			   )
	private List<Diary> diarie = new ArrayList<>();	// Diary の user フィールドを参照
}
