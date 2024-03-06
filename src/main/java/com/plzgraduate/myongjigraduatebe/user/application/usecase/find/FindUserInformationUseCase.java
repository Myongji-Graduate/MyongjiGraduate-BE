package com.plzgraduate.myongjigraduatebe.user.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;

public interface FindUserInformationUseCase {

	UserInformationResponse findUserInformation(Long userId);
}
