package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;           // ページング結果用
import org.springframework.data.domain.Pageable;       // ページング情報（ページ番号・件数）
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // ────────── 完了状態で検索（全件） ──────────
    List<Todo> findByCompleted(boolean completed);  // 完了/未完了でList取得

    // ────────── 完了状態で検索（ページング） ──────────
    Page<Todo> findByCompleted(boolean completed, Pageable pageable); // 完了/未完了でPage取得

    // ────────── 部分一致検索（キーワード + 完了状態） ──────────
    List<Todo> findByTitleContainingAndCompleted(String title, boolean completed); // List版
    Page<Todo> findByTitleContainingAndCompleted(String title, boolean completed, Pageable pageable); // Page版

    // ────────── 並び替え用メソッド（List版） ──────────
    List<Todo> findByCompletedOrderByDueDateAscDueTimeAsc(boolean completed);    // 期限順
    List<Todo> findByCompletedOrderByCategoryNameAsc(boolean completed);         // カテゴリ順
    List<Todo> findByCompletedOrderByAccountAsc(boolean completed);              // 担当者順
    List<Todo> findByCompletedOrderByTitleAsc(boolean completed);                // タイトル順

    // ────────── 並び替え用メソッド（Page版） ──────────
    Page<Todo> findByCompletedOrderByDueDateAscDueTimeAsc(boolean completed, Pageable pageable); // 期限順
    Page<Todo> findByCompletedOrderByCategoryNameAsc(boolean completed, Pageable pageable);      // カテゴリ順
    Page<Todo> findByCompletedOrderByAccountAsc(boolean completed, Pageable pageable);           // 担当者順
    Page<Todo> findByCompletedOrderByTitleAsc(boolean completed, Pageable pageable);             // タイトル順

    // ────────── 日付検索（List版） ──────────
    List<Todo> findByDueDate(LocalDate dueDate);                                        // 日付のみ
    List<Todo> findByDueDateAndCompleted(LocalDate dueDate, boolean completed);         // 日付+完了フラグ

    // ────────── 日付検索（Page版） ──────────
    Page<Todo> findByDueDateAndCompleted(LocalDate dueDate, boolean completed, Pageable pageable); // 完了タスクのみ取得
    Page<Todo> findAllByDueDate(LocalDate dueDate, Pageable pageable);	// 全タスク取得（カレンダー用）
}
