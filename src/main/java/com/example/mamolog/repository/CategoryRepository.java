package com.example.mamolog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findById(Long id);
	Category findByName(String name);
}
