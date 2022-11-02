package com.plzgraduate.myongjigraduatebe.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  User findUserById(Long id);
}
