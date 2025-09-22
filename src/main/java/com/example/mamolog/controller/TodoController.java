package com.example.mamolog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepository;      			// TodoのDB操作用
    private final CategoryRepository categoryRepository;		// CategoryのDB操作用

    public TodoController(TodoRepository todoRepository, CategoryRepository categoryRepository) {		// リポジトリをDI
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    // ────────── 一覧表示 ──────────
    @GetMapping
    public String listTodos(Model model) {
        List<Todo> todoList = todoRepository.findByCompleted(false);		// 未完了タスクの取得
        List<Todo> completedList = todoRepository.findByCompleted(true);	// 完了タスクの取得

        // null安全対策・空リスト保証
        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());
        
        return "todos/list";		// index.html を表示
    }

    // ────────── 新規作成フォーム表示 ──────────
    @GetMapping("/new")
    public String newTodoForm(Model model) {
    	model.addAttribute("todo", new Todo());		// 新規Todo用の空オブジェクトをフォームにバインド
    	
    	// カテゴリ一覧を取得して Model に追加
    	List<Category> categories = categoryRepository.findAll();
    	model.addAttribute("categories", categories);    	
        
        return "todos/todo-new"; 				// 新規作成画面
    }

    // ────────── 新規作成保存 ──────────
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo, 
    						 RedirectAttributes redirectAttributes) {
        todoRepository.save(todo); 												// TodoをDBに保存
        redirectAttributes.addFlashAttribute("message", "Todoを作成しました"); 	// 完了メッセージ
        return "redirect:/todos"; 												// 作成後は一覧へリダイレクト
    }

    // ────────── 編集フォーム表示 ──────────
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
       
        model.addAttribute("todo", existing);           // 編集対象をViewへ渡す
        
        // カテゴリも渡す（ドロップダウン用）
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        return "todos/todo-edit"; 						// 編集画面
    }

    // ────────── 編集保存 ──────────
    @PostMapping("/{id}/update")
    public String updateTodoForm(@PathVariable Long id,
    							 @ModelAttribute Todo todo,
    							 RedirectAttributes redirectAttributes) {
        // DBから既存Todoを取得
    	Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));

        // フィールドを上書き（フォームから受け取ったtodoの値をexistingにsetterで上書き）
        existing.setTitle(todo.getTitle());										// タスク名
        existing.setMemo(todo.getMemo());										// メモ
        existing.setAccount(todo.getAccount());									// 担当者
        existing.setDueDate(todo.getDueDate());									// 更新日
        existing.setDueTime(todo.getDueTime());									// 更新時間
        existing.setCompleted(todo.isCompleted());								// 完了タスク
        existing.setCategory(todo.getCategory()); 								// カテゴリも更新

        todoRepository.save(existing); 											// DBに保存
        redirectAttributes.addFlashAttribute("message", "Todoを更新しました"); 	// 更新メッセージ
        
        return "redirect:/todos"; 												// 一覧へリダイレクト
    }

    // ────────── 削除処理 ──────────
    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
    	// DBから既存Todoを取得
    	Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
        
        todoRepository.delete(existing); 										// 削除
        
        redirectAttributes.addFlashAttribute("message", "Todoを削除しました"); 	// 削除メッセージ
        return "redirect:/todos"; 												// 一覧へリダイレクト
    }
    
 // ────────── 完了トグル ──────────
    @GetMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo ID:" + id));

        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);

        redirectAttributes.addFlashAttribute("message", "完了状態を変更しました");
        return "redirect:/todos";
    }
}
