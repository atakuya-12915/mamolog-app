package com.example.mamolog.controller.diary;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mamolog.entity.Diary;
import com.example.mamolog.repository.DiaryRepository;
import com.example.mamolog.security.UserDetailsImpl;
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

        return "diaries/diary-list";
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
                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                         @RequestParam(value = "photoFile", required = false) MultipartFile photo,
                         RedirectAttributes redirectAttributes) {
        
        // バリデーション: ログインチェック
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "ログインしてください");
            return "redirect:/login";
        }
        
        // バリデーション: 内容が空でないかチェック
        if (diary.getContent() == null || diary.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "日記の内容を入力してください");
            return "redirect:/diaries/new";
        }

        try {
            if (diary.getDiaryDate() == null) {
                diary.setDiaryDate(LocalDate.now());
            }

            if (photo != null && !photo.isEmpty()) {
                String filename = fileStorageService.store(photo);
                diary.setPhotoFilename(filename);
            }
            
            diary.setUser(userDetails.getUser());
            diaryRepository.save(diary);
            redirectAttributes.addFlashAttribute("success", "日記を登録しました");
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "写真のアップロードに失敗しました");
            return "redirect:/diaries/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "日記の保存に失敗しました");
            return "redirect:/diaries/new";
        }
        
        return "redirect:/diaries";
    }

    // -------------------------------
    // 編集フォーム表示
    // -------------------------------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, 
                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Optional<Diary> opt = diaryRepository.findById(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "日記が見つかりません");
            return "redirect:/diaries";
        }
        
        Diary diary = opt.get();
        // 自分の日記かチェック
        if (!diary.getUser().getId().equals(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("error", "他のユーザーの日記は編集できません");
            return "redirect:/diaries";
        }
        
        model.addAttribute("diary", diary);
        return "diaries/diary-edit";
    }

    // -------------------------------
    // 更新処理（写真差し替え可能）
    // -------------------------------
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @ModelAttribute Diary form,
                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                         @RequestParam(value = "photoFile", required = false) MultipartFile photo,
                         @RequestParam(value = "removePhoto", required = false) String removePhotoFlag,
                         RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Diary diary = diaryRepository.findById(id).orElseThrow();
            
            // 自分の日記かチェック
            if (!diary.getUser().getId().equals(userDetails.getUser().getId())) {
                redirectAttributes.addFlashAttribute("error", "他のユーザーの日記は編集できません");
                return "redirect:/diaries";
            }
            
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
            redirectAttributes.addFlashAttribute("success", "日記を更新しました");
            return "redirect:/diaries/" + id;
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "写真の処理に失敗しました");
            return "redirect:/diaries/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "日記の更新に失敗しました");
            return "redirect:/diaries/" + id + "/edit";
        }
    }

    // -------------------------------
    // 削除処理
    // -------------------------------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Optional<Diary> opt = diaryRepository.findById(id);
        if (opt.isPresent()) {
            Diary d = opt.get();
            
            // 自分の日記かチェック
            if (!d.getUser().getId().equals(userDetails.getUser().getId())) {
                redirectAttributes.addFlashAttribute("error", "他のユーザーの日記は削除できません");
                return "redirect:/diaries";
            }
            
            if (d.getPhotoFilename() != null) {
                fileStorageService.delete(d.getPhotoFilename());
            }
            diaryRepository.delete(d);
            redirectAttributes.addFlashAttribute("success", "日記を削除しました");
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