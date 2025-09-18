package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	// 今日のタスク全件取得
    List<Todo> findByDueDateTime(LocalDate dueDateTime);

    // 完了済み/未完了タスクを取得
    List<Todo> findByCompleted(boolean completed);

    // 今日の未完了タスクを期限順で取得
    List<Todo> findByCompletedFalseAndDueDateTimeOrderByDueDateTimeAsc(LocalDate dueDateTime);

    // 完了タスクをカウント
    long countByCompletedTrueAndDueDateTime(LocalDate dueDateTime);

    // 今日の完了タスクを期限順で取得
    List<Todo> findByCompletedTrueAndDueDateTimeOrderByDueDateTimeAsc(LocalDate dueDateTime);

    // カテゴリーIDでTodoを取得
    List<Todo> findByCategoryId(Long categoryId);
}
