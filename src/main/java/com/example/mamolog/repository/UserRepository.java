package com.example.mamolog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
	// Optional でユーザーデータが存在しない場合の null 安全対策
	Optional<User> findByEmail(String email);

}
