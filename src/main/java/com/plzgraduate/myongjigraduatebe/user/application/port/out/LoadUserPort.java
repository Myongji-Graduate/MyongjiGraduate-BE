package com.plzgraduate.myongjigraduatebe.user.application.port.out;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadUserPort {
	User loadUserByUserId(String userId);
}
