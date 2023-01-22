package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserById(long id);

  boolean existsByUserIdOrStudentNumber(
      UserId userId,
      StudentNumber studentNumber
  );

  Optional<User> findByUserId(UserId userId);

  boolean existsByUserId(UserId userId);

  boolean existsByStudentNumber(StudentNumber studentNumber);

  @Query("select u from User u left join fetch u.department where u.id = :id")
  Optional<User> findByIdWithFetchJoin(@Param("id") long id);

  Optional<User> findUserByStudentNumber(StudentNumber studentNumber);
}
