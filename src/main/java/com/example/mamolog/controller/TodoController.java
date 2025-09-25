package com.example.mamolog.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.service.TodoService;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ────────── 一覧表示（未完了 / 完了タスクを取得） ──────────
    @GetMapping
    public String listTodos(Model model) {
        List<Todo> todoList = todoService.getTodosByCompleted(false); // 未完了
        List<Todo> completedList = todoService.getTodosByCompleted(true); // 完了
        
        // null 空配列保証
        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());
    	return "todos/list";	// list.html 一覧画面表示
    }

    // ────────── 新規作成フォーム表示 ──────────
    @GetMapping("/new")
    public String newTodoForm(@RequestParam(required = false) Long categoryId, Model model) {
        Todo todo = new Todo();
        
        // カテゴリIDが指定されていたら設定
        if (categoryId != null) {
            todoService.getCategory(categoryId).ifPresent(todo::setCategory);
        }
        model.addAttribute("todo", todo);
        model.addAttribute("categories", todoService.getAllCategories());
        return "todos/todo-new";	// todo-new.html 新規作成フォーム表示
    }

    // ────────── 新規作成・保存 -------------------
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo,
                             @RequestParam(required = false) String newCategoryName,
                             RedirectAttributes redirectAttributes) {
        todoService.createTodo(todo, newCategoryName);
        redirectAttributes.addFlashAttribute("message", "Todoを作成しました");
        return "redirect:/todos";	// Todo 一覧画面にリダイレクト
    }

    // ────────── 編集フォーム表示 -------------------
    @GetMapping("/{id}/edit")
    public String editTodoForm(@PathVariable Long id, Model model) {        
        model.addAttribute("todo", todoService.getTodo(id));
        model.addAttribute("categories", todoService.getAllCategories());
        return "todos/todo-edit";	// todo-edit.html 編集画面にリダイレクト
    }

    // ────────── 更新 -------------------
    @PostMapping("/{id}/update")
    public String updateTodo(@PathVariable Long id,
                             @ModelAttribute Todo todo,
                             RedirectAttributes redirectAttributes) {
        todoService.updateTodo(id, todo);
        redirectAttributes.addFlashAttribute("message", "Todoを更新しました");
        return "redirect:/todos";	// Todo 一覧画面にリダイレクト
    }

    // ────────── 削除 -------------------
    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id);
        redirectAttributes.addFlashAttribute("message", "Todoを削除しました");
        return "redirect:/todos";		// Todo 一覧画面にリダイレクト
    }

    // ────────── 完了トグル -------------------
    @GetMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        todoService.toggleTodo(id);		// 完了状態を反転
        redirectAttributes.addFlashAttribute("message", "完了状態を変更しました");
        return "redirect:/todos";		// Todo 一覧画面にリダイレクト
    }
    
 // ────────── キーワード検索 ──────────
    @GetMapping("/search")
    public String searchTodos(@RequestParam(name = "keyword", required = false) String keyword,
    						  Model model) {
        model.addAttribute("todoList", todoService.searchTodos(keyword, false));		// 未完了
        model.addAttribute("completedList", todoService.searchTodos(keyword, true));	// 完了
        model.addAttribute("keyword", keyword);											// 入力欄に保持
        return "todos/list";
    }

    // ────────── ソート + 日付絞り込み ──────────
    @GetMapping("/sort")
    public String sortTodos(@RequestParam(required = false) String sortBy,
    						@RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            Model model) {
    	List<Todo> todoList;
    	List<Todo> completedList;
    	
    	if (date != null) {
            // 日付指定あり → その日付のタスクのみ
            todoList = todoService.getTodosByDate(date, false);
            completedList = todoService.getTodosByDate(date, true);
            model.addAttribute("date", date);
        } else {
            // 通常のソート
            todoList = todoService.sortTodos(sortBy, false);
            completedList = todoService.sortTodos(sortBy, true);
            model.addAttribute("sortBy", sortBy);
        }

        model.addAttribute("todoList", todoList);
        model.addAttribute("completedList", completedList);
        return "todos/list";
    }
}
