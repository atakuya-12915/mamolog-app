package com.example.mamolog.controller.diary;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.repository.DiaryRepository;
import com.example.mamolog.service.FileStorageService;

@Controller
@RequestMapping("/diaries")
public class DiaryController {

    private final DiaryRepository diaryRepository;
    private final FileStorageService fileStorageService;

    public DiaryController(DiaryRepository diaryRepository, FileStorageService fileStorageService) {
        this.diaryRepository = diaryRepository;
        this.fileStorageService = fileStorageService;
    }

    // -------------------------------
    // 日記一覧表示（ページング対応）
    // -------------------------------
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("diaryDate").descending());
        Page<Diary> diaryPage = diaryRepository.findAll(pageable);

        model.addAttribute("diaryPage", diaryPage);
        model.addAttribute("currentPage", page);

        return "diaries/diary-list"; // templates/diaries/diary-list.html
    }

    // -------------------------------
    // 新規作成フォーム表示
    // -------------------------------
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("diary", new Diary());
        return "diaries/diary-new";
    }

    // -------------------------------
    // 登録処理（写真1枚）
    // -------------------------------
    @PostMapping("/create")
    public String create(@ModelAttribute Diary diary,
                         @RequestParam("photoFile") MultipartFile photo) throws IOException {

        if (diary.getDiaryDate() == null)
            diary.setDiaryDate(LocalDate.now());

        if (photo != null && !photo.isEmpty()) {
            String filename = fileStorageService.store(photo);		// uploads/ フォルダに photo のコピー保存
            diary.setPhotoFilename(filename);						// photoFilename に UUID付ファイル名を返す
        }
        diaryRepository.save(diary);								//　DBに diary と photoFilename が保存される
        return "redirect:/diaries";
    }

    // -------------------------------
    // 編集フォーム表示
    // -------------------------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Diary> opt = diaryRepository.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/diaries";
        }
        model.addAttribute("diary", opt.get());
        return "diaries/diary-edit";
    }

    // -------------------------------
    // 更新処理（写真差し替え可能）
    // -------------------------------
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @ModelAttribute Diary form,
                         @RequestParam(value = "photoFile", required = false) MultipartFile photo,
                         @RequestParam(value = "removePhoto", required = false) String removePhotoFlag) throws IOException {

        Diary diary = diaryRepository.findById(id).orElseThrow();
        diary.setContent(form.getContent());
        diary.setDiaryDate(form.getDiaryDate() != null ? form.getDiaryDate() : diary.getDiaryDate());

        if ("on".equals(removePhotoFlag) && diary.getPhotoFilename() != null) {
            fileStorageService.delete(diary.getPhotoFilename());
            diary.setPhotoFilename(null);
        }

        if (photo != null && !photo.isEmpty()) {
            if (diary.getPhotoFilename() != null) {
                fileStorageService.delete(diary.getPhotoFilename());
            }
            String filename = fileStorageService.store(photo);
            diary.setPhotoFilename(filename);
        }

        diaryRepository.save(diary);
        return "redirect:/diaries/" + id;
    }

    // -------------------------------
    // 削除処理
    // -------------------------------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        Optional<Diary> opt = diaryRepository.findById(id);
        if (opt.isPresent()) {
            Diary d = opt.get();
            if (d.getPhotoFilename() != null) {
                fileStorageService.delete(d.getPhotoFilename());
            }
            diaryRepository.delete(d);
        }
        return "redirect:/diaries";
    }

    // -------------------------------
    // 詳細表示
    // -------------------------------
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        model.addAttribute("diary", diary);
        return "diaries/diary-detail";
    }

    // -------------------------------
    // 日付で検索（カレンダー連携用）
    // -------------------------------
    @GetMapping("/date/{date}")
    public String findByDate(@PathVariable String date, Model model) {
        LocalDate d = LocalDate.parse(date);
        Optional<Diary> opt = diaryRepository.findByDiaryDate(d);
        model.addAttribute("diary", opt.orElse(null));
        return "diaries/diary-detail";
    }
}
