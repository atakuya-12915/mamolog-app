package com.example.mamolog.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)	// カテゴリ名の「unll・重複禁止」
	private String name;
	
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)	// Category削除時にTodo側でも削除する
	private List<Todo> todos;
	
	// コンストラクタ
	public Category() {}
	
	public Category(String name) {
		this.name = name;
	}	
}
