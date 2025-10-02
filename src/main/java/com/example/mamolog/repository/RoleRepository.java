package com.example.mamolog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mamolog.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
