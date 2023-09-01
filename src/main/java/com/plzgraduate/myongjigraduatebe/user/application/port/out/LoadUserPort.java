package com.plzgraduate.myongjigraduatebe.user.application.port.out;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadUserPort {

	User loadUserById(Long id);
	User loadUserByAuthId(String authId);
}
