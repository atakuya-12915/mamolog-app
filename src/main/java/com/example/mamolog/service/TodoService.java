package com.example.mamolog.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.entity.User;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

/**
 * TodoService - ビジネスロジック層
 * 役割: 複雑なロジック、トランザクション管理、データ加工を担当
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    // ================================================
    // 基本的な取得（ページネーション）
    // ================================================
    
    /**
     * ユーザーのTodoを完了状態別に取得（ページネーション・作成日時降順）
     */
    public Page<Todo> getTodosByUser(User user, boolean completed, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return todoRepository.findByUserAndCompleted(user, completed, pageable);
    }
    
    /**
     * 全ユーザーのTodoを完了状態別に取得（カレンダー用）
     * 変更可能なArrayListを返す
     */
    public List<Todo> getTodosByCompleted(boolean completed) {
        return new ArrayList<>(
            todoRepository.findAll().stream()
                .filter(t -> t.isCompleted() == completed)
                .toList()
        );
    }
    
    /**
     * 特定ユーザーの特定日付のTodoを全件取得（REST API用）
     */
    public List<Todo> getAllTodosByDateAndCompleted(User user, LocalDate date, boolean completed) {
        return todoRepository.findAll().stream()
            .filter(t -> t.getUser().equals(user))
            .filter(t -> t.getDueDate() != null && t.getDueDate().equals(date))
            .filter(t -> t.isCompleted() == completed)
            .toList();
    }
    
    // ================================================
    // カレンダー用メソッド
    // ================================================
    
    /**
     * 特定日付のTodoを取得（ページネーション対応）
     */
    public Page<Todo> findByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueTime").ascending());
        // 全ユーザーの特定日付のTodoを取得
        List<Todo> allTodos = todoRepository.findAll().stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().equals(date))
            .sorted((t1, t2) -> {
                if (t1.getDueTime() == null) return 1;
                if (t2.getDueTime() == null) return -1;
                return t1.getDueTime().compareTo(t2.getDueTime());
            })
            .toList();
        
        // ページング処理
        int start = page * size;
        int end = Math.min(start + size, allTodos.size());
        List<Todo> pageContent = allTodos.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent, pageable, allTodos.size());
    }
    
    // ================================================
    // ソート機能（ページネーション）
    // ================================================
    
    /**
     * ソート機能付きTodo取得
     */
    public Page<Todo> getTodosSorted(User user, boolean completed, String sortBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        switch (sortBy) {
            case "account":
                return todoRepository.findByUserAndCompletedOrderByAccount(user, completed, pageable);
            case "category":
                return todoRepository.findByUserAndCompletedOrderByCategory(user, completed, pageable);
            case "dueDate":
                return todoRepository.findByUserAndCompletedOrderByDueDate(user, completed, pageable);
            default:
                return getTodosByUser(user, completed, page, size);
        }
    }
    
    // ================================================
    // 検索機能（ページネーション）
    // ================================================
    
    /**
     * キーワード検索
     */
    public Page<Todo> searchTodos(User user, boolean completed, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return todoRepository.searchByUserAndKeyword(user, completed, keyword, pageable);
    }
    
    /**
     * 日付フィルタ
     */
    public Page<Todo> getTodosByDate(User user, boolean completed, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return todoRepository.findByUserAndCompletedAndDueDate(user, completed, date, pageable);
    }
    
    // ================================================
    // CRUD操作
    // ================================================
    
    /**
     * ID指定でTodo取得
     */
    public Todo getTodo(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです: " + id));
    }
    
    /**
     * 新規作成
     */
    @Transactional
    public void createTodo(Todo todo, String newCategoryName, User user) {
        // 新規カテゴリ処理
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            Category existing = categoryRepository.findByName(newCategoryName);
            if (existing == null) {
                Category newCategory = new Category();
                newCategory.setName(newCategoryName);
                categoryRepository.save(newCategory);
                todo.setCategory(newCategory);
            } else {
                todo.setCategory(existing);
            }
        }
        
        todo.setUser(user);
        todoRepository.save(todo);
    }
    
    /**
     * 更新
     */
    @Transactional
    public void updateTodo(Long id, Todo updated, String newCategoryName) {
        Todo existing = getTodo(id);
        
        // 新規カテゴリ処理
        Category categoryToSet = null;
        if (newCategoryName != null && !newCategoryName.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            categoryRepository.save(newCategory);
            categoryToSet = newCategory;
        } else if (updated.getCategory() != null && updated.getCategory().getId() != null) {
            categoryToSet = updated.getCategory();
        }
        
        // 更新
        existing.setTitle(updated.getTitle());
        existing.setMemo(updated.getMemo());
        existing.setAccount(updated.getAccount());
        existing.setDueDate(updated.getDueDate());
        existing.setDueTime(updated.getDueTime());
        existing.setCompleted(updated.isCompleted());
        existing.setCategory(categoryToSet);
        
        todoRepository.save(existing);
    }
    
    /**
     * 削除
     */
    @Transactional
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }
    
    /**
     * 完了トグル
     */
    @Transactional
    public void toggleTodo(Long id) {
        Todo todo = getTodo(id);
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
    }
    
    // ================================================
    // カテゴリ関連
    // ================================================
    
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}