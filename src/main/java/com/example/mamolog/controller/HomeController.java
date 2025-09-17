package com.example.mamolog.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.TodoRepository;

@Controller
public class HomeController {
	private final TodoRepository todoRepository;

	// todoRepositoryのコンストラクタをDI
	public HomeController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@GetMapping("/home") // Home画面(index.html)を表示
	public String showHome(Model model) {
		LocalDate today = LocalDate.now(); // 今日の日付を取得

		// 今日の未完了タスク一覧を取得
		List<Todo> todoList = todoRepository.findByCompletedFalseAndDueDateTimeOrderByIdAsc(today);
		
		// 今日の完了タスク件数
		long completedCount = todoRepository.countByCompletedTrueAndDueDateTime(today); 
		
		// 今日の完了タスク一覧（確認用）
		List<Todo> completedList = todoRepository.findByCompletedFalseAndDueDateTimeOrderByIdAsc(today);

		// Model に格納して View に渡す
		model.addAttribute("todoList", todoList);
		model.addAttribute("completedCount", completedCount);
		model.addAttribute("completedList" ,completedList);

		return "index";
	}
}
