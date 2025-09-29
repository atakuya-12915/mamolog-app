package com.example.mamolog.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.DiaryRepository;
import com.example.mamolog.repository.TodoRepository;

@Controller
public class HomeController {
	private final TodoRepository todoRepository;		// Todo用
	private final DiaryRepository diaryRepository;		// Diary用

	// todoRepositoryのコンストラクタをDI
	public HomeController(TodoRepository todoRepository, DiaryRepository diaryRepository) {
		this.todoRepository = todoRepository;
		this.diaryRepository = diaryRepository;
	}

	@GetMapping("/home")
	public String showHome(Model model) {
		List<Todo> todoList = todoRepository.findByCompleted(false);		// 未完了タスクの取得
        List<Todo> completedList = todoRepository.findByCompleted(true);	// 完了タスクの取得
        long remainingCount = todoList.size();								// 未完了タスクの件数
        
        // 「昨日の日記」表示用        
        Optional<Diary> y = diaryRepository.findByDiaryDate(LocalDate.now().minusDays(1));
        
        // null 安全のため空リストを保証
        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());
		
        model.addAttribute("remainingCount" ,remainingCount);				// 未完了タスクの件数
        model.addAttribute("yesterdayDiary", y.orElse(null));				// 日記用

		return "index";		// /home → index.html
	}
}
