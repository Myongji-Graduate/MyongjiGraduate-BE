package com.plzgraduate.myongjigraduatebe.user.application.port.out;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadUserPort {
	User loadUserByAuthId(String authId);
}
