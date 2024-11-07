package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteTakenLecturePort {

	void deleteAllTakenLecturesByUser(User user);

	void deleteTakenLectureById(Long deleteId);
}
