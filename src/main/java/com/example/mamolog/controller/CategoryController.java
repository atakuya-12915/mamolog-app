package com.example.mamolog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.mamolog.entity.Category;
import com.example.mamolog.repository.CategoryRepository;

@Controller
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryRepository categoryRepository;
	
	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	// Ajax用：カテゴリを追加して JSON を返す
    @PostMapping("/add")
    @ResponseBody
    public Category add(@RequestParam String name, @RequestParam(required = false) String color) {
        Category category = new Category();
        category.setName(name);
        // Category に color カラムを追加している場合はセットする
        // c.setColor(color);
        return categoryRepository.save(category);
    }
}
