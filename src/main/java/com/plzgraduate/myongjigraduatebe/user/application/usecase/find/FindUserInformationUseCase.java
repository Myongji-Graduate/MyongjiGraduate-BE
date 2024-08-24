package com.plzgraduate.myongjigraduatebe.user.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindUserInformationUseCase {

	User findUserInformation(Long userId);
}
