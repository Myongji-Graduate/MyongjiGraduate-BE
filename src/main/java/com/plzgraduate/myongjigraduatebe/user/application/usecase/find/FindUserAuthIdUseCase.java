package com.plzgraduate.myongjigraduatebe.user.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.user.api.findauthid.dto.response.UserAuthIdResponse;

public interface FindUserAuthIdUseCase {

	UserAuthIdResponse findUserAuthId(String studentNumber);
}
