package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteTakenLecturePort {
	void deleteTakenLecturesByUser(User user);
}
