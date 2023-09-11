package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteTakenLecturePort {
	void deleteAllTakenLecturesByUser(User user);

	void deleteTakenLecturesByIds(List<Long> deleteIds);
}
