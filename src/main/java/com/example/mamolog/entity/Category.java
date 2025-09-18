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
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
	@ToString.Exclude			// Entityの双方向参照を無効化のため
	@EqualsAndHashCode.Exclude	// Entityの双方向参照を無効化のため
	private List<Todo> todos;
	
	// JPA用無引数コンストラクタ
    public Category() {}

    // 引数付きコンストラクタ
    public Category(String name) { this.name = name; }
    
 // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
