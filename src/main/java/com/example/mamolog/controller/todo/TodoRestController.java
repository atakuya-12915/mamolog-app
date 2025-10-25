package com.example.mamolog.controller.todo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.security.UserDetailsImpl;
import com.example.mamolog.service.TodoService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TodoRestController - REST API用
 * カレンダー表示用のTodoデータをJSON形式で返す
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoRestController {
    
    private final TodoService todoService;
    
    /**
     * Todo一覧取得（JSON形式）
     * 日付指定ありの場合: /api/todos?date=2025-01-20
     * 完了状態指定: /api/todos?completed=true
     */
    @GetMapping
    public List<TodoDto> getTodos(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "false") boolean completed,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<Todo> todos;
        
        // 日付指定がある場合
        if (date != null) {
            // ユーザー指定がある場合（ログイン済み）
            if (userDetails != null) {
                // ページネーション不要なので、全件取得用のメソッドを呼ぶ
                todos = todoService.getAllTodosByDateAndCompleted(
                    userDetails.getUser(), date, completed);
            } else {
                // 未ログインの場合は空リスト
                todos = List.of();
            }
        }
        // 日付指定がない場合
        else {
            todos = todoService.getTodosByCompleted(completed);
        }
        
        // Todo → DTO に変換してJSON形式で返す
        return todos.stream()
                .map(todo -> new TodoDto(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getDueDate() != null ? 
                        todo.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null,
                    todo.isCompleted()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * DTO (Data Transfer Object)
     * JSON形式でクライアントに返すデータ構造
     */
    @Data
    static class TodoDto {
        private Long id;
        private String title;
        private String date;
        private boolean completed;
        
        public TodoDto(Long id, String title, String date, boolean completed) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.completed = completed;
        }
    }
}