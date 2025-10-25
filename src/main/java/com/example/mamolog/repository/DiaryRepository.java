package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.entity.User;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    /**
     * 指定日付の日記を取得(Home画面:昨日の日記表示用)
     */
    Optional<Diary> findByDiaryDate(LocalDate diaryDate);
    
    /**
     * カレンダーで当月分をまとめて取得
     */
    List<Diary> findByDiaryDateBetween(LocalDate start, LocalDate end);
    
    /**
     * 特定ユーザーの日記一覧を取得(ページネーション対応)
     */
    Page<Diary> findByUser(User user, Pageable pageable);
    
    /**
     * 特定ユーザーの日記を日付で取得
     */
    Optional<Diary> findByUserAndDiaryDate(User user, LocalDate diaryDate);
    
    /**
     * 特定ユーザーの指定期間の日記一覧を取得
     */
    List<Diary> findByUserAndDiaryDateBetween(User user, LocalDate start, LocalDate end);
    
    /**
     * 特定ユーザーの日記件数をカウント
     */
    long countByUser(User user);
    
    /**
     * 特定ユーザーの日記を新しい順に取得
     */
    List<Diary> findByUserOrderByDiaryDateDesc(User user);
    
    /**
     * 写真が添付されている日記のみ取得
     */
    @Query("SELECT d FROM Diary d WHERE d.user = :user AND d.photoFilename IS NOT NULL ORDER BY d.diaryDate DESC")
    List<Diary> findDiariesWithPhotoByUser(@Param("user") User user);
    
    /**
     * 指定年月の日記一覧を取得(カレンダー表示用)
     */
    @Query("SELECT d FROM Diary d WHERE d.user = :user " +
           "AND YEAR(d.diaryDate) = :year " +
           "AND MONTH(d.diaryDate) = :month " +
           "ORDER BY d.diaryDate ASC")
    List<Diary> findByUserAndYearMonth(@Param("user") User user, 
                                       @Param("year") int year, 
                                       @Param("month") int month);
}