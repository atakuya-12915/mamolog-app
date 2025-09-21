package com.example.mamolog.controller;

import java.util.ArrayList;
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

	@GetMapping("/home")
	public String showHome(Model model) {
		List<Todo> todoList = todoRepository.findByCompleted(false);		// 未完了タスクの取得
        List<Todo> completedList = todoRepository.findByCompleted(true);	// 完了タスクの取得
        long remainingCount = todoList.size();							// 未完了タスクの件数

        // null 安全のため空リストを保証
        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());
		
        model.addAttribute("remainingCount" ,remainingCount);

		return "index";		// /home → index.html
	}
}
