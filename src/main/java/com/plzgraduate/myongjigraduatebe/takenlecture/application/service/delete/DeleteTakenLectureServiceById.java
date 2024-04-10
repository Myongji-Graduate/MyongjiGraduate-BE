package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
public class DeleteTakenLectureServiceById implements DeleteTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@Override
	public void deleteTakenLecture(Long userId, Long deletedTakenLectureId) {
		User user = findUserUseCase.findUserById(userId);
		deleteTakenLecturePort.deleteTakenLectureById(deletedTakenLectureId);
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);
	}
}
