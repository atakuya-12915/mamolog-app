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

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoController(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    // ────────── 一覧表示 ──────────
    @GetMapping
    public String listTodos(Model model) {
        List<Todo> todoList = todoRepository.findByCompleted(false);
        List<Todo> completedList = todoRepository.findByCompleted(true);

        model.addAttribute("todoList", todoList != null ? todoList : new ArrayList<>());
        model.addAttribute("completedList", completedList != null ? completedList : new ArrayList<>());

        return "todos/list";
    }

    // ────────── 新規作成フォーム表示 ──────────
    @GetMapping("/new")
    public String newTodoForm(@RequestParam(required = false) Long categoryId, Model model) {
        Todo todo = new Todo();
        // パラメータでカテゴリIDが渡された場合は事前選択
        if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            todo.setCategory(cat);
        }
        model.addAttribute("todo", todo);

        // カテゴリ一覧を取得
        model.addAttribute("categories", categoryRepository.findAll());

        return "todos/todo-new";
    }

    // ────────── 新規作成・保存（カテゴリ新規登録対応） ──────────
    @PostMapping
    public String createTodo(
            @ModelAttribute Todo todo,
            @RequestParam(required = false) String newCategoryName, // 新規カテゴリ名
            RedirectAttributes redirectAttributes) {

        // 新規カテゴリ名が入力されていた場合、DBに保存してTodoにセット
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            categoryRepository.save(newCategory);
            todo.setCategory(newCategory);
        }

        todoRepository.save(todo);
        redirectAttributes.addFlashAttribute("message", "Todoを作成しました");
        return "redirect:/todos";
    }

    // ────────── 編集フォーム表示 ──────────
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
        model.addAttribute("todo", existing);
        model.addAttribute("categories", categoryRepository.findAll());
        return "todos/todo-edit";
    }

    // ────────── 編集保存 ──────────
    @PostMapping("/{id}/update")
    public String updateTodoForm(@PathVariable Long id,
                                 @ModelAttribute Todo todo,
                                 RedirectAttributes redirectAttributes) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));

        existing.setTitle(todo.getTitle());
        existing.setMemo(todo.getMemo());
        existing.setAccount(todo.getAccount());
        existing.setDueDate(todo.getDueDate());
        existing.setDueTime(todo.getDueTime());
        existing.setCompleted(todo.isCompleted());
        existing.setCategory(todo.getCategory());

        todoRepository.save(existing);
        redirectAttributes.addFlashAttribute("message", "Todoを更新しました");
        return "redirect:/todos";
    }

    // ────────── 削除処理 ──────────
    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Todo existing = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
        todoRepository.delete(existing);
        redirectAttributes.addFlashAttribute("message", "Todoを削除しました");
        return "redirect:/todos";
    }

    // ────────── 完了トグル ──────────
    @GetMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
        redirectAttributes.addFlashAttribute("message", "完了状態を変更しました");
        return "redirect:/todos";
    }
}
