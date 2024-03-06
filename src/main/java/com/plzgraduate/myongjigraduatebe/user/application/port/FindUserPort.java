package com.plzgraduate.myongjigraduatebe.user.application.port;

import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindUserPort {

	Optional<User> findUserById(Long id);
	Optional<User> findUserByAuthId(String authId);

	Optional<User> findUserByStudentNumber(String studentNumber);
}
