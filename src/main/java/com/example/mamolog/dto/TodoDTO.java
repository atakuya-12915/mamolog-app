package com.example.mamolog.dto;

import com.example.mamolog.entity.Todo;

public class TodoDTO {
    private Long id;
    private String title;
    private String categoryName;
    private boolean completed;

    public TodoDTO(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.completed = todo.isCompleted();
        this.categoryName = todo.getCategory() != null ? todo.getCategory().getName() : "未設定";
    }

    // Getterのみ
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategoryName() { return categoryName; }
    public boolean isCompleted() { return completed; }
}