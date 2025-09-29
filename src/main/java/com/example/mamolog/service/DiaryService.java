package com.example.mamolog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;

	public Page<Diary> getDiaryPage(int page, int size) {
		return diaryRepository.findAll(PageRequest.of(page, size));
	}

	public Diary save(Diary diary) {
		return diaryRepository.save(diary);
	}

	public void delete(Long id) {
		diaryRepository.deleteById(id);
	}

	public Diary findById(Long id) {
		return diaryRepository.findById(id).orElse(null);
	}
}
