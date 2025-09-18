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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

import jakarta.transaction.Transactional;

// Todo関連の画面遷移・処理を担当するコントローラー
@Controller
@RequestMapping("/todos") // /todos 配下のURLにマッピング
public class TodoController {

    private final TodoRepository todoRepository;      // TodoのDB操作用
    private final CategoryRepository categoryRepository; // CategoryのDB操作用

    // コンストラクタでリポジトリを注入
    public TodoController(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    // ────────── 一覧表示 ──────────
    @GetMapping
    @Transactional  // Lazy loading 安全化
    public String listTodos(Model model) {
        List<Todo> todoList = todoRepository.findByCompleted(false);
        List<Todo> completedList = todoRepository.findByCompleted(true);

        // null 安全のため空リストを保証
        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());

        return "index";
    }

    // ────────── 新規作成フォーム表示 ──────────
    @GetMapping("/new")
    public String newTodoForm(Model model) {
        model.addAttribute("todo", new Todo());                    // 空のTodoオブジェクトをフォームにバインド
        model.addAttribute("categories", categoryRepository.findAll()); // カテゴリ選択用
        return "todos/todo-new"; // 新規作成画面
    }

    // ────────── 新規作成処理 ──────────
    @PostMapping
    public String createTodo(@ModelAttribute Todo todo,
                             @RequestParam(required = false) String newCategoryName,
                             RedirectAttributes redirectAttributes) {
    	
    	Category category = null;
    	
        // 新しいカテゴリが入力されていればDBに追加
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            category = categoryRepository.findByName(newCategoryName)
            	.orElseGet(() -> {
            		Category newCat = new Category();
                    newCat.setName(newCategoryName);
                    return categoryRepository.save(newCat);
                });
        } else if (todo.getCategory() != null) {
            category = todo.getCategory();
        }
        
        todo.setCategory(category);
        todoRepository.save(todo); 		// TodoをDBに保存

        redirectAttributes.addFlashAttribute("message", "Todoを作成しました"); 	// 完了メッセージ
        return "redirect:/todos"; 		// 作成後は一覧へリダイレクト
    }

    // ────────── 編集フォーム表示 ──────────
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));

        model.addAttribute("todo", existing);                 // 編集対象をViewへ渡す
        model.addAttribute("categories", categoryRepository.findAll()); // カテゴリ選択用
        return "todos/todo-edit"; // 編集画面
    }

    // ────────── 更新処理 ──────────
    @PostMapping("/{id}/update")
    public String updateTodoForm(@PathVariable Long id,
                                 @ModelAttribute Todo todo,
                                 RedirectAttributes redirectAttributes) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));

        // フィールドをまとめて更新
        existing.setTitle(todo.getTitle());
        existing.setMemo(todo.getMemo());
        existing.setAccount(todo.getAccount());
        existing.setDueDateTime(todo.getDueDateTime());
        existing.setCategory(todo.getCategory());
        existing.setCompleted(todo.isCompleted());

        todoRepository.save(existing); // DBに保存

        redirectAttributes.addFlashAttribute("message", "Todoを更新しました"); // 更新メッセージ
        return "redirect:/todos"; // 一覧へリダイレクト
    }

    // ────────── 削除 ──────────
    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
        todoRepository.delete(existing); // 削除
        redirectAttributes.addFlashAttribute("message", "Todoを削除しました"); // 削除メッセージ
        return "redirect:/todos"; // 一覧へリダイレクト
    }

    // ────────── 完了トグル ──────────
    @GetMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));

        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);

        redirectAttributes.addFlashAttribute("message", "Todoの完了状態を変更しました");
        return "redirect:/todos";
    }
}
