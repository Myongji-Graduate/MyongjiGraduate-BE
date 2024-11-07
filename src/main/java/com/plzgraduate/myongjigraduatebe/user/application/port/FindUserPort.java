package com.plzgraduate.myongjigraduatebe.user.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Optional;

public interface FindUserPort {

	Optional<User> findUserById(Long id);

	Optional<User> findUserByAuthId(String authId);

	Optional<User> findUserByStudentNumber(String studentNumber);
}
