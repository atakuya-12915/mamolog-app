package com.example.mamolog.controller.todo;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.entity.User;
import com.example.mamolog.security.UserDetailsImpl;
import com.example.mamolog.service.TodoService;

import lombok.RequiredArgsConstructor;

/**
 * TodoController - プレゼンテーション層
 * 役割: HTTPリクエストの処理、ビューへのデータ渡し、バリデーション
 */
@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {
    
    private final TodoService todoService;
    
    // ================================================
    // 一覧表示（ページネーション・タブ切替対応）
    // ================================================
    
    @GetMapping
    public String list(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int incompletePage,
            @RequestParam(defaultValue = "0") int completedPage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date,
            Model model) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = userDetails.getUser();
        Page<Todo> incompleteTodos;
        Page<Todo> completedTodos;
        
        // 検索処理
        if (keyword != null && !keyword.isEmpty()) {
            incompleteTodos = todoService.searchTodos(user, false, keyword, incompletePage, 10);
            completedTodos = todoService.searchTodos(user, true, keyword, completedPage, 10);
            model.addAttribute("keyword", keyword);
        }
        // 日付フィルタ
        else if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            incompleteTodos = todoService.getTodosByDate(user, false, filterDate, incompletePage, 10);
            completedTodos = todoService.getTodosByDate(user, true, filterDate, completedPage, 10);
            model.addAttribute("date", date);
        }
        // ソート処理
        else if (sortBy != null && !sortBy.isEmpty()) {
            incompleteTodos = todoService.getTodosSorted(user, false, sortBy, incompletePage, 10);
            completedTodos = todoService.getTodosSorted(user, true, sortBy, completedPage, 10);
            model.addAttribute("sortBy", sortBy);
        }
        // デフォルト（作成日時降順）
        else {
            incompleteTodos = todoService.getTodosByUser(user, false, incompletePage, 10);
            completedTodos = todoService.getTodosByUser(user, true, completedPage, 10);
        }
        
        model.addAttribute("incompleteTodos", incompleteTodos);
        model.addAttribute("completedTodos", completedTodos);
        model.addAttribute("incompletePage", incompletePage);
        model.addAttribute("completedPage", completedPage);
        
        return "todos/todo-list";
    }
    
    // ================================================
    // 新規作成
    // ================================================
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("todo", new Todo());
        model.addAttribute("categories", todoService.getAllCategories());
        return "todos/todo-new";
    }
    
    @PostMapping
    public String create(
            @ModelAttribute Todo todo,
            BindingResult bindingResult,
            @RequestParam(required = false) String newCategoryName,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", todoService.getAllCategories());
            return "todos/todo-new";
        }
        
        todoService.createTodo(todo, newCategoryName, userDetails.getUser());
        redirectAttributes.addFlashAttribute("success", "Todoを作成しました");
        return "redirect:/todos";
    }
    
    // ================================================
    // 編集
    // ================================================
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("todo", todoService.getTodo(id));
        model.addAttribute("categories", todoService.getAllCategories());
        return "todos/todo-edit";
    }
    
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @ModelAttribute Todo todo,
            @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
            RedirectAttributes redirectAttributes) {
        
        todoService.updateTodo(id, todo, newCategoryName);
        redirectAttributes.addFlashAttribute("success", "Todoを更新しました");
        return "redirect:/todos";
    }
    
    // ================================================
    // 削除
    // ================================================
    
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        todoService.deleteTodo(id);
        redirectAttributes.addFlashAttribute("success", "Todoを削除しました");
        return "redirect:/todos";
    }
    
    // ================================================
    // 完了トグル
    // ================================================
    
    @PostMapping("/{id}/toggle")
    public String toggle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Todo todo = todoService.getTodo(id);
        
        if (!todo.getUser().getId().equals(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("error", "他のユーザーのTodoは変更できません");
            return "redirect:/todos";
        }
        
        todoService.toggleTodo(id);
        return "redirect:/todos";
    }
}