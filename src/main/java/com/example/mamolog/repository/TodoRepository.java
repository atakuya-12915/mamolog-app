package com.example.mamolog.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 完了/未完了リスト取得
    List<Todo> findByCompleted(boolean completed);
    
    // 指定の条件でTodoの存在チェック
    boolean existsByTitleAndDueDateAndDueTime(String title, LocalDate dueDate, LocalTime DueTime);
}
