package com.example.mamolog.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

//Todo関連の画面遷移・処理を担当するコントローラー
@Controller
@RequestMapping("/todos")
public class TodoController {
	
	private final TodoRepository todoRepository;
	private final CategoryRepository categoryRepository;
	
	// コンストラクタでリポジトリを注入
	public TodoController(TodoRepository todoRepository, CategoryRepository categoryRepository) {
		this.todoRepository = todoRepository;
		this.categoryRepository = categoryRepository;
	}
	
	// Todo一覧表示
	@GetMapping
	public String listTodos(Model model) {
		LocalDate today = LocalDate.now();		//　現在日時を取得
	    List<Todo> todos = todoRepository.findByDueDateTime(today);	//　DBから「今日の日付」の全Todoを取得
	    model.addAttribute("todos", todos);

	    // 残りタスク数の計算
	    long totalCount = todos.size();
	    long completedCount = todos.stream().filter(Todo::isCompleted).count();
	    model.addAttribute("totalCount", totalCount);
	    model.addAttribute("completedCount", completedCount);

	    return "todos/list";
	}
	
	
	
	
}