package com.example.mamolog.controller.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

// カレンダー・日毎の詳細画面表示用のコントローラー
@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final TodoService todoService;

    public CalendarController(TodoService todoService) {
        this.todoService = todoService;
    }
    
    // fullCalendar.html（カレンダー）を表示
    @GetMapping
    public String showCalendar() {
    	
    	return "calendar/fullCalendar";		// fullCalendar.html を返す
    }

    // ────────── 日付クリック時の詳細表示（ページング対応） ──────────
    @GetMapping("/detail")
    public String showDetail(@RequestParam("date") String date,
    						 @RequestParam(value = "page", defaultValue = "0") int page,
    						 @RequestParam(value = "size", defaultValue = "10") int size, 
    						 Model model) {
    	// カレンダーでクリックされた日付
        LocalDate selectedDate = LocalDate.parse(date);
        
        // その日(selectedDate)の全タスク
        Page<Todo> todosPage = todoService.findByDate(selectedDate, page, size);	// ページ情報付きの箱
        List<Todo> todos = todosPage.getContent();		// 実際に表示する分だけ Todoリスト を取り出す
        if (todos == null) todos = new ArrayList<>();	// Todoがない場合は空配列をViewに渡す

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("todos", todos);
        model.addAttribute("currentPage", page);						// 現在のページ番号
        model.addAttribute("totalPages", todosPage.getTotalPages());	// 全ページ数

        return "calendar/detail"; // calendar/detail.html
    }
}
