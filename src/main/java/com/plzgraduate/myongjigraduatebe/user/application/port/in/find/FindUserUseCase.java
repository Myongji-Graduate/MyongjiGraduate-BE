package com.plzgraduate.myongjigraduatebe.user.application.port.in.find;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindUserUseCase {
	User findUserById(Long id);

	User findUserByAuthId(String authId);
}
