package com.example.mamolog.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.mamolog.entity.Category;
import com.example.mamolog.entity.Todo;
import com.example.mamolog.repository.CategoryRepository;
import com.example.mamolog.repository.TodoRepository;

@Configuration // Springに設定クラスとして認識させる
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepo, TodoRepository todoRepo) {
        return args -> {
            // カテゴリ作成
            Category work = new Category("仕事");
            Category privateCategory = new Category("プライベート");

            categoryRepo.save(work); // DBに保存
            categoryRepo.save(privateCategory);

            // Todo作成（カテゴリ紐付け）
            Todo todo1 = new Todo();
            todo1.setTitle("資料作成");
            todo1.setCategory(work);

            Todo todo2 = new Todo();
            todo2.setTitle("買い物");
            todo2.setCategory(privateCategory);

            todoRepo.save(todo1);
            todoRepo.save(todo2);

            System.out.println("初期データを投入しました");
        };
    }
}
