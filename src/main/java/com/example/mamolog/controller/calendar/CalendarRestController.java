package com.example.mamolog.controller.calendar;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * CalendarRestController - カレンダー用REST API
 * FullCalendarにTodoデータをJSON形式で返す
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarRestController {
    
    private final TodoService todoService;
    
    /**
     * カレンダー用Todo一覧取得
     * 未完了・完了の全てのTodoを返す
     */
    @GetMapping("/todos")
    public List<CalendarTodoDto> getTodos() {
        
        // 未完了Todoを取得
        List<Todo> incompleteTodos = todoService.getTodosByCompleted(false);
        
        // 完了Todoを取得
        List<Todo> completedTodos = todoService.getTodosByCompleted(true);
        
        // 2つのリストを結合（新しいArrayListを作成）
        List<Todo> allTodos = new ArrayList<>();
        allTodos.addAll(incompleteTodos);
        allTodos.addAll(completedTodos);
        
        // 日付フォーマット定義（FullCalendar用: YYYY-MM-DD）
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        // Todo → DTO に変換してJSON形式で返す
        return allTodos.stream()
                .filter(todo -> todo.getDueDate() != null) // 日付がnullのものは除外
                .map(todo -> new CalendarTodoDto(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getDueDate().format(formatter),
                    todo.isCompleted()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * DTO (Data Transfer Object)
     * FullCalendarに渡すデータ構造
     */
    @Data
    static class CalendarTodoDto {
        private Long id;
        private String title;
        private String start;      // FullCalendar用の開始日（YYYY-MM-DD）
        private boolean completed;
        
        public CalendarTodoDto(Long id, String title, String start, boolean completed) {
            this.id = id;
            this.title = title;
            this.start = start;
            this.completed = completed;
        }
    }
}