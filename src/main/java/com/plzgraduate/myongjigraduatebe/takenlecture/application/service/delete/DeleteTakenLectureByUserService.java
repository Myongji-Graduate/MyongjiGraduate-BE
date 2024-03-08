package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class DeleteTakenLectureByUserService implements DeleteTakenLectureByUserUseCase {

	private final DeleteTakenLecturePort deleteTakenLecturePort;
	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		deleteTakenLecturePort.deleteAllTakenLecturesByUser(user);
	}
}
