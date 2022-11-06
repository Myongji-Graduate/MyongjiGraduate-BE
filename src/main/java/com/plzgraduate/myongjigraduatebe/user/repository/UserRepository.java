package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserById(long id);

  boolean existsByUserIdOrStudentNumber(
      UserId userId,
      StudentNumber studentNumber
  );
}
