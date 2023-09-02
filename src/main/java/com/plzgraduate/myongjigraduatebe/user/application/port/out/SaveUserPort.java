package com.plzgraduate.myongjigraduatebe.user.application.port.out;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface SaveUserPort {
	void saveUser(User user);
}
