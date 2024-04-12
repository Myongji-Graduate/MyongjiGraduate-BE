package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteTakenLectureUseCase {
	void deleteAllTakenLecturesByUser(User user);

	void deleteTakenLecture(Long userId, Long deletedTakenLectureId);

}
