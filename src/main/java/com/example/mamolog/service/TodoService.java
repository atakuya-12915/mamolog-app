package com.example.mamolog.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }
        
    // CRUD系メソッド
    // ---------- 未完了/完了Todo取得 -------------------
    public List<Todo> getTodosByCompleted(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }

    // ---------- 単体Todo取得 -------------------
    public Todo getTodo(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id));
    }

    // ---------- 単体カテゴリ取得 -------------------
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }
    
    // ---------- 全カテゴリ取得 -------------------
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // ---------- 新規作成 -------------------
    public void createTodo(Todo todo, String newCategoryName) {
    	// 新規カテゴリ名がある場合
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            categoryRepository.save(newCategory);	// 先にカテゴリ保存
            todo.setCategory(newCategory);
        }
        todoRepository.save(todo);		// Todoを保存
    }

    // ---------- 更新 -------------------
    public void updateTodo(Long id, Todo updated) {
        Todo existing = getTodo(id);	// DBから既存Todoを取得
        // フォーム送信された値で上書き
        existing.setTitle(updated.getTitle());
        existing.setMemo(updated.getMemo());
        existing.setAccount(updated.getAccount());
        existing.setDueDate(updated.getDueDate());
        existing.setDueTime(updated.getDueTime());
        existing.setCompleted(updated.isCompleted());
        existing.setCategory(updated.getCategory());
        todoRepository.save(existing);	// 更新データを保存
    }

    // ---------- 削除 -------------------
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    // ---------- 完了トグル -------------------
    public void toggleTodo(Long id) {
        Todo todo = getTodo(id);
        todo.setCompleted(!todo.isCompleted());		// 完了フラグを反転
        todoRepository.save(todo);
    }   
    
    // ────────── キーワード検索 ──────────
    public List<Todo> searchTodos(String keyword, boolean completed) {
    	if (keyword == null || keyword.isEmpty()) {
            return getTodosByCompleted(completed);		// キーワード未入力なら全件
        }
        return todoRepository.findByTitleContainingAndCompleted(keyword, completed);
    }
    
    // ────────── 日付で絞り込み ──────────
    public List<Todo> getTodosByDate(LocalDate date, boolean completed) {
        // Repositoryに「期限日と完了状態で検索するメソッド」を定義して呼び出す
        return todoRepository.findByDueDateAndCompleted(date, completed);
    }

    // ────────── ソート処理 / Reposiotory側でソート ──────────
    public List<Todo> sortTodos(String sortBy, boolean completed) {
    // sortBy の値に応じて切り替え
    switch (sortBy) {
    case "dueDate":  // 期限日でソート
        return todoRepository.findByCompletedOrderByDueDateAscDueTimeAsc(completed);
    case "category": // カテゴリでソート
        return todoRepository.findByCompletedOrderByCategoryNameAsc(completed);
    case "account":  // 担当者でソート
        return todoRepository.findByCompletedOrderByAccountAsc(completed);
    default:         // 指定がない場合はそのまま
        return getTodosByCompleted(completed);
    }
}
    
    // ---------- 日付検索 -------------------
    public List<Todo> findTodosByDate(LocalDate date, boolean completed) {
        return todoRepository.findByDueDateAndCompleted(date, completed);
    }
}
