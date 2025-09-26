package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // ────────── 完了状態で検索 ──────────
    List<Todo> findByCompleted(boolean completed);

    // ────────── 重複チェック ──────────
    /* boolean existsByTitleAndDueDateAndDueTime(String title, LocalDate dueDate, LocalTime dueTime); */

    // ────────── 部分一致検索（キーワード + 完了状態） ──────────
    List<Todo> findByTitleContainingAndCompleted(String title, boolean completed);

    // ────────── 並び替え用メソッド ──────────
    List<Todo> findByCompletedOrderByDueDateAscDueTimeAsc(boolean completed);       // 期限順
    List<Todo> findByCompletedOrderByCategoryNameAsc(boolean completed);            // カテゴリ順
    List<Todo> findByCompletedOrderByAccountAsc(boolean completed);                 // 担当者順    
    List<Todo> findByCompletedOrderByTitleAsc(boolean completed);                   // タイトル順

    // ────────── 日付検索 ──────────
    List<Todo> findByDueDate(LocalDate dueDate);									// 日付のみ
    List<Todo> findByDueDateAndCompleted(LocalDate dueDate, boolean completed);		// 完了フラグ判定あり
}