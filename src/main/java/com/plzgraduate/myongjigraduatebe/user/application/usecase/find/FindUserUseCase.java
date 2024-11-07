package com.plzgraduate.myongjigraduatebe.user.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindUserUseCase {

	User findUserById(Long id);

	User findUserByAuthId(String authId);

	User findUserByStudentNumber(String studentNumber);
}
