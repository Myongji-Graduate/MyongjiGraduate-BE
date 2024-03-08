package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteTakenLectureByUserUseCase {
	void deleteAllTakenLecturesByUser(User user);
}
