package com.example.mamolog.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

import lombok.Data;

//────────── REST APIでJSON返却するController ──────────
@RestController
@RequestMapping("/api/calendar")
public class CalendarRestController {

	private final TodoService todoService;
	
	public CalendarRestController(TodoService todoService) {
		this.todoService = todoService;
	}
	
	// ────────── カレンダー用Todo一覧取得 ──────────
	@GetMapping("/todos")
	public List<CalendarTodoDto> getTodos() {
		
		// Todo一覧を取得（完了・未完了すべて）
		List<Todo> todos = todoService.getTodosByCompleted(false);	// 全ての未完了タスク
		todos.addAll(todoService.getTodosByCompleted(true));			// 完了タスクも追加
		
		// 日付フォーマット定義（FullCalendarが理解する YYYY-MM-DD）
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		
		// Todo → DTO に変換
		return todos.stream()							// .stream() リストを1件ずつTodoを処理
					.map(todo -> new CalendarTodoDto(	// .map() 必要な情報だけをDTOに変換
							todo.getId(),
							todo.getTitle(),
							todo.getDueDate() != null ? todo.getDueDate().format(formatter) : null,		// 日付文字列： null の場合はFullCalendar に渡さない
							todo.isCompleted()			// 完了状態
					))									
					.collect(Collectors.toList());	// List に戻して返す（REST APIでJSONに変換される）				
	}
	
	// ────────── DTO（Data Transfer Object） ──────────
    @Data
    static class CalendarTodoDto {
        private Long id;          		// TodoのID
        private String title;     		// カレンダーに表示するタイトル
        private String start;     		// FullCalendar用の開始日（YYYY-MM-DD）
        private boolean completed;		// 完了状態

        public CalendarTodoDto(Long id, String title, String start, boolean completed) {
            this.id = id;
            this.title = title;
            this.start = start;
            this.completed = completed;
        }
    }
}
