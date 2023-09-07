package com.plzgraduate.myongjigraduatebe.takenlecture.application.service;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
public class DeleteTakenLectureByUserService implements DeleteTakenLectureByUserUseCase {

	private final DeleteTakenLecturePort deleteTakenLecturePort;
	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		deleteTakenLecturePort.deleteAllTakenLecturesByUser(user);
	}
}
