package com.example.mamolog.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

	// 指定の日記を取得（Home画面：昨日の日記表示用）
	Optional<Diary> findByDiaryDate(LocalDate diaryDate);
	
	// カレンダーで当月分をまとめて取得
	List<Diary> findByDiaryDateBetween(LocalDate start, LocalDate end);
}
