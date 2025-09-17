package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	// 今日のタスク全件取得
	List<Todo> findByDueDateTime(LocalDate dueDateTime);
		
	// 指定した日付の未完了タスクをID昇順で取得（完了タスクは除く）
    List<Todo> findByCompletedFalseAndDueDateTimeOrderByIdAsc(LocalDate dueDateTime);

    // 完了タスク件数
    long countByCompletedTrueAndDueDateTime(LocalDate dueDateTime);
    
    // 完了タスク一覧
    List<Todo> findByCompletedTrueAndDueDateTimeOrderByIdAsc(LocalDate dueDateTime);
    
    //　カテゴリーIDでTodoを取得する
    List<Todo> findByCategoryId(Long categoryId);
}
