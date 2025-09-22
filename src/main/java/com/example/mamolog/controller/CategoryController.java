package com.example.mamolog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mamolog.entity.Category;
import com.example.mamolog.repository.CategoryRepository;

@Controller
@RequestMapping("/todos/categories")
public class CategoryController {
	
	private CategoryRepository categoryRepository;
	
	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// ────────── 一覧表示 ──────────
	@GetMapping
	public String listCategories(Model model) {
		// DBから全てのカテゴリを取得
		model.addAttribute("categories", categoryRepository.findAll());
		
		return "category"; 		// category.html を表示
	}
	
	// ────────── 新規カテゴリ登録・保存（todo） ──────────
	@PostMapping("/add")
	public String addCategory(
			@RequestParam String name,					// フォームから送信されるカテゴリ名
			@RequestParam(required = false) Long todoId	// Todo新規作成から呼ばれた場合はIDを受け取る
	) {
		
		// 新しいカテゴリを作成
		Category newCategory = new Category();
		newCategory.setName(name);
		categoryRepository.save(newCategory);		// DBに保存
		
		// todoIdがある場合はTodo作成画面にリダイレクト
        if (todoId != null) {
            return "redirect:/todos/new?categoryId=" + newCategory.getId();
        }

		return "redirect:/todos/categories";	// カテゴリ一覧にリダイレクト
	}
}