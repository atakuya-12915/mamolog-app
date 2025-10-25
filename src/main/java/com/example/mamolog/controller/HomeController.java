package com.example.mamolog.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.DiaryRepository;
import com.example.mamolog.security.UserDetailsImpl;
import com.example.mamolog.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final TodoService todoService;
    private final DiaryRepository diaryRepository;
    
    @GetMapping("/home")
    public String home(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // 未完了Todoをページネーション取得（10件単位・作成日時降順）
        Page<Todo> todoPage = todoService.getTodosByUser(
            userDetails.getUser(), false, page, 10);
        
        // 昨日の日記を取得
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Optional<Diary> yesterdayDiary = diaryRepository.findByUserAndDiaryDate(
            userDetails.getUser(), yesterday);
        
        model.addAttribute("todoPage", todoPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("yesterdayDiary", yesterdayDiary.orElse(null));
        
        return "index";
    }
    
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/home";
    }
}