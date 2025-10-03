package com.example.mamolog.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;                  // ページング情報を扱うクラス
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;              // ページング用インターフェース
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional  // メソッド内のDB操作はトランザクション管理
public class TodoService {

    private final TodoRepository todoRepository;           // Todo用リポジトリ
    private final CategoryRepository categoryRepository;   // Category用リポジトリ

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;             // コンストラクタでDI
        this.categoryRepository = categoryRepository;
    }

    // ────────── 未完了/完了Todo一覧取得（List） ──────────
    public List<Todo> getTodosByCompleted(boolean completed) {
        return todoRepository.findByCompleted(completed); // 完了フラグで取得
    }

    // ────────── 未完了/完了Todo一覧取得（Page） ──────────
    public Page<Todo> getTodosPage(boolean completed, Pageable pageable) {
        return todoRepository.findByCompleted(completed, pageable); // ページング対応で取得
    }

    // ────────── 単体Todo取得 ──────────
    public Todo getTodo(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なTodo IDです:" + id)); // 存在しなければ例外
    }

    // ────────── 単体カテゴリ取得 ──────────
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id); // IDでカテゴリ取得
    }

    // ────────── 全カテゴリ取得 ──────────
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(); // 全カテゴリ取得
    }

    // ────────── 新規作成 ──────────
    public void createTodo(Todo todo, String newCategoryName) {
        // 新規カテゴリ名が入力されている場合
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            Category newCategory = new Category();        // カテゴリオブジェクト作成
            newCategory.setName(newCategoryName);        // 名前をセット
            categoryRepository.save(newCategory);        // DBに保存
            todo.setCategory(newCategory);               // Todoに紐付け
        }
        todoRepository.save(todo);                       // TodoをDBに保存
    }

    // ────────── 更新 ──────────
    public void updateTodo(Long id, Todo updated, String newCategoryName) {

        Category categoryToSet = null;  // 最終的にセットするカテゴリ

        // 新規カテゴリが入力されている場合
        if (newCategoryName != null && !newCategoryName.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(newCategoryName);
            categoryRepository.save(newCategory);       // 新規カテゴリをDBに保存
            categoryToSet = newCategory;
        }
        // 既存カテゴリが選択されている場合
        else if (updated.getCategory() != null && updated.getCategory().getId() != null) {
            categoryToSet = updated.getCategory();
        }

        // 既存Todoを取得
        Todo existing = todoRepository.findById(id).orElseThrow();

        // フォームの値で更新
        existing.setTitle(updated.getTitle());
        existing.setMemo(updated.getMemo());
        existing.setAccount(updated.getAccount());
        existing.setDueDate(updated.getDueDate());
        existing.setDueTime(updated.getDueTime());
        existing.setCompleted(updated.isCompleted());
        existing.setCategory(categoryToSet);             // nullでも安全

        todoRepository.save(existing);                   // DBに保存
    }

    // ────────── 削除 ──────────
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);                  // ID指定で削除
    }

    // ────────── 完了トグル ──────────
    public void toggleTodo(Long id) {
        Todo todo = getTodo(id);                        // Todo取得
        todo.setCompleted(!todo.isCompleted());        // 完了フラグ反転
        todoRepository.save(todo);                      // 保存
    }

    // ────────── キーワード検索（List） ──────────
    public List<Todo> searchTodos(String keyword, boolean completed) {
        if (keyword == null || keyword.isEmpty()) {
            return getTodosByCompleted(completed);     // キーワード未入力なら全件
        }
        return todoRepository.findByTitleContainingAndCompleted(keyword, completed);
    }

    // ────────── キーワード検索（Page） ──────────
    public Page<Todo> searchTodosPage(String keyword, boolean completed, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return todoRepository.findByCompleted(completed, pageable); // ページング対応
        }
        return todoRepository.findByTitleContainingAndCompleted(keyword, completed, pageable);
    }

    // ────────── 日付検索（List） ──────────
    public List<Todo> getTodosByDate(LocalDate date, boolean completed) {
        return todoRepository.findByDueDateAndCompleted(date, completed); // 日付と完了フラグで取得
    }

    // ────────── 日付検索（Page） ──────────
    public Page<Todo> getTodosByDatePage(LocalDate date, boolean completed, Pageable pageable) {
        return todoRepository.findByDueDateAndCompleted(date, completed, pageable); // ページング対応
    }
    
    // ────────── カレンダー用 日付検索（Page） ──────────
    public Page<Todo> findByDate(LocalDate date, int page, int size) {
    	Pageable pageable = PageRequest.of(page, size, Sort.by("dueTime").ascending());
    	return todoRepository.findAllByDueDate(date, pageable);
    }

    // ────────── ソート（List） ──────────
    public List<Todo> sortTodos(String sortBy, boolean completed) {
        switch (sortBy) {
            case "dueDate":  // 期限日でソート
                return todoRepository.findByCompletedOrderByDueDateAscDueTimeAsc(completed);
            case "category": // カテゴリ名でソート
                return todoRepository.findByCompletedOrderByCategoryNameAsc(completed);
            case "account":  // 担当者でソート
                return todoRepository.findByCompletedOrderByAccountAsc(completed);
            default:         // 指定なしの場合はそのまま
                return getTodosByCompleted(completed);
        }
    }

    // ────────── ソート（Page） ──────────
    public Page<Todo> sortTodosPage(String sortBy, boolean completed, Pageable pageable) {
        switch (sortBy) {
            case "dueDate":
                return todoRepository.findByCompletedOrderByDueDateAscDueTimeAsc(completed, pageable);
            case "category":
                return todoRepository.findByCompletedOrderByCategoryNameAsc(completed, pageable);
            case "account":
                return todoRepository.findByCompletedOrderByAccountAsc(completed, pageable);
            default:
                return todoRepository.findByCompleted(completed, pageable);
        }
    }
}
