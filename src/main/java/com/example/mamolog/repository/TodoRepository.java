package com.example.mamolog.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mamolog.entity.Todo;
import com.example.mamolog.entity.User;

/**
 * TodoRepository - データアクセス層
 * 役割: データベースへの問い合わせのみを担当
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    // ================================================
    // 基本的な取得（ページネーション対応）
    // ================================================
    
    /**
     * ユーザーと完了状態でページネーション取得（作成日時降順）
     */
    Page<Todo> findByUserAndCompleted(User user, boolean completed, Pageable pageable);
    
    /**
     * ユーザーでページネーション取得（作成日時降順）
     */
    Page<Todo> findByUser(User user, Pageable pageable);
    
    // ================================================
    // ソート機能（ページネーション対応）
    // ================================================
    
    /**
     * 担当者順（ページネーション対応）
     */
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = :completed " +
           "ORDER BY t.account ASC, t.createdAt DESC")
    Page<Todo> findByUserAndCompletedOrderByAccount(
        @Param("user") User user, 
        @Param("completed") boolean completed, 
        Pageable pageable);
    
    /**
     * カテゴリ順（ページネーション対応）
     */
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = :completed " +
           "ORDER BY t.category.name ASC, t.createdAt DESC")
    Page<Todo> findByUserAndCompletedOrderByCategory(
        @Param("user") User user, 
        @Param("completed") boolean completed, 
        Pageable pageable);
    
    /**
     * 期限日順（ページネーション対応）
     */
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = :completed " +
           "ORDER BY t.dueDate ASC NULLS LAST, t.dueTime ASC NULLS LAST, t.createdAt DESC")
    Page<Todo> findByUserAndCompletedOrderByDueDate(
        @Param("user") User user, 
        @Param("completed") boolean completed, 
        Pageable pageable);
    
    // ================================================
    // 検索機能（ページネーション対応）
    // ================================================
    
    /**
     * キーワード検索（ページネーション対応）
     */
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = :completed " +
           "AND (t.title LIKE %:keyword% OR t.memo LIKE %:keyword%) " +
           "ORDER BY t.createdAt DESC")
    Page<Todo> searchByUserAndKeyword(
        @Param("user") User user,
        @Param("completed") boolean completed,
        @Param("keyword") String keyword,
        Pageable pageable);
    
    /**
     * 日付フィルタ（ページネーション対応）
     */
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = :completed " +
           "AND t.dueDate = :date ORDER BY t.createdAt DESC")
    Page<Todo> findByUserAndCompletedAndDueDate(
        @Param("user") User user,
        @Param("completed") boolean completed,
        @Param("date") LocalDate date,
        Pageable pageable);
    
    // ================================================
    // カウント機能
    // ================================================
    
    /**
     * ユーザーのTodo件数をカウント
     */
    long countByUser(User user);
    
    /**
     * ユーザーの未完了/完了Todo件数をカウント
     */
    long countByUserAndCompleted(User user, boolean completed);
}