package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserById(long id);
}
