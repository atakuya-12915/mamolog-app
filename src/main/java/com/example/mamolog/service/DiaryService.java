package com.example.mamolog.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.entity.User;
import com.example.mamolog.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryService {
    
    private final DiaryRepository diaryRepository;

    /**
     * ページネーション付き日記一覧取得(全ユーザー)
     */
    public Page<Diary> getDiaryPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("diaryDate").descending());
        return diaryRepository.findAll(pageable);
    }

    /**
     * 特定ユーザーの日記一覧取得(ページネーション付き)
     */
    public Page<Diary> getDiaryPageByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("diaryDate").descending());
        return diaryRepository.findByUser(user, pageable);
    }

    /**
     * 日記の新規作成
     */
    @Transactional
    public Diary save(Diary diary) {
        if (diary.getUser() == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        
        if (diary.getDiaryDate() == null) {
            diary.setDiaryDate(LocalDate.now());
        }
        
        Diary savedDiary = diaryRepository.save(diary);
        log.info("Diary saved: id={}, userId={}, date={}", 
            savedDiary.getId(), 
            savedDiary.getUser().getId(), 
            savedDiary.getDiaryDate());
        
        return savedDiary;
    }

    /**
     * 日記の削除
     */
    @Transactional
    public void delete(Long id) {
        if (!diaryRepository.existsById(id)) {
            throw new IllegalArgumentException("Diary not found: id=" + id);
        }
        diaryRepository.deleteById(id);
        log.info("Diary deleted: id={}", id);
    }

    /**
     * 日記をIDで取得
     */
    public Diary findById(Long id) {
        return diaryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Diary not found: id=" + id));
    }

    /**
     * 特定の日付の日記を取得
     */
    public Diary findByDiaryDate(LocalDate date) {
        return diaryRepository.findByDiaryDate(date).orElse(null);
    }

    /**
     * 指定期間の日記一覧を取得(カレンダー表示用)
     */
    public List<Diary> findByDateRange(LocalDate start, LocalDate end) {
        return diaryRepository.findByDiaryDateBetween(start, end);
    }

    /**
     * 特定ユーザーの指定期間の日記一覧を取得
     */
    public List<Diary> findByUserAndDateRange(User user, LocalDate start, LocalDate end) {
        return diaryRepository.findByUserAndDiaryDateBetween(user, start, end);
    }

    /**
     * 特定ユーザーの日記件数を取得
     */
    public long countByUser(User user) {
        return diaryRepository.countByUser(user);
    }

    /**
     * 特定ユーザーの最新の日記を取得
     */
    public Diary findLatestByUser(User user) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("diaryDate").descending());
        Page<Diary> page = diaryRepository.findByUser(user, pageable);
        return page.hasContent() ? page.getContent().get(0) : null;
    }
}