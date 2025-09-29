package com.example.mamolog.controller.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

@RestController
@RequestMapping("/api/todos")
public class TodoRestController {
	
	private final TodoService todoService;
	
	public TodoRestController(TodoService todoService) {
		this.todoService = todoService;
	}
	
	// ────────── 全件取得 ──────────
	@GetMapping
	public List<Todo> getTodos(
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam(required = false, defaultValue = "false") boolean completed
	) {
		// 日付指定がある場合（例：/api/todos?date=2025-09-26&completed=false）
		if (date != null) {
			return todoService.getTodosByDate(date, completed);		// 指定日付の未完了タスクを全件取得してJSON形式で返す
		}
		// 日付指定がない場合
		return todoService.getTodosByCompleted(completed);			// 未完了タスクを全件取得してJSON形式で返す
	}
}