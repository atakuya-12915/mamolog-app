package com.example.mamolog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "diaries")
@Data
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                      // 主キー

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;               // コメント本文

    @Column(nullable = false)
    private LocalDate diaryDate;          // 日記の日付

    private String photoFilename;         // 保存した写真のファイル名

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;      // 作成日時

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;      // 更新日時
    
 // User との関連を追加
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
