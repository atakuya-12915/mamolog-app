package com.example.mamolog.service;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.TodoRepository;

public class TodoService {
	
	private TodoRepository todoRepository;
	
	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}
	
	public Todo createTodo(Todo todo) {
		// 同じタイトル・同じ日付・同じ時間のTodoが既に存在するか確認
		boolean exists = todoRepository.existsByTitleAndDueDateAndDueTime(
			todo.getTitle(),
			todo.getDueDate(),
			todo.getDueTime()
		);
		
		if (exists) {
			throw new IllegalArgumentException("同じタイトル・日付・時間のTodoが既に存在します。");
		}
		
		return todoRepository.save(todo);
	}

}
