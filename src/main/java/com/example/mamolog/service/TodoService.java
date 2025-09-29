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
    public void updateTodo(Long id, Todo updated, String newCategoryName) {
    	
    	Category categoryToSet = null;  // 最終的にセットするカテゴリ
        
    	// 新規カテゴリが入力されている場合
    	if (newCategoryName != null && !newCategoryName.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            categoryRepository.save(newCategory);  // 新規カテゴリをDBに保存
            categoryToSet = newCategory;
        } 
        // 既存カテゴリが選択されている場合
        else if (updated.getCategory() != null && updated.getCategory().getId() != null) {
            categoryToSet = updated.getCategory();
        }
        // どちらもない場合は categoryToSet = null

        // 既存Todo取得
        Todo existing = todoRepository.findById(id).orElseThrow();

        // フォーム値で更新
        existing.setTitle(updated.getTitle());
        existing.setMemo(updated.getMemo());
        existing.setAccount(updated.getAccount());
        existing.setDueDate(updated.getDueDate());
        existing.setDueTime(updated.getDueTime());
        existing.setCompleted(updated.isCompleted());
        existing.setCategory(categoryToSet);  // nullの場合も安全にセット可能

        todoRepository.save(existing);  // 保存
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
    
    // Search機能
    // ────────── キーワード検索 ──────────
    public List<Todo> searchTodos(String keyword, boolean completed) {
    	if (keyword == null || keyword.isEmpty()) {
            return getTodosByCompleted(completed);		// キーワード未入力なら全件
        }
        return todoRepository.findByTitleContainingAndCompleted(keyword, completed);
    }
     
    // ────────── 日付検索（日付＋完了フラグ）──────────
    public List<Todo> findByDate(LocalDate date) {
        return todoRepository.findByDueDate(date);
    }

    // Sort機能
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
    
    // ────────── 日付検索（日付＋完了フラグ）──────────
    public List<Todo> getTodosByDate(LocalDate date, boolean completed) {
        // Repositoryに「期限日と完了状態で検索するメソッド」を定義して呼び出す
        return todoRepository.findByDueDateAndCompleted(date, completed);
    }
}