package com.example.mamolog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	Category findByName(String name);
}
