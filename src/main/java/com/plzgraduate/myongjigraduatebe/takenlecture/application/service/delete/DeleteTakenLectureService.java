package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
class DeleteTakenLectureService implements DeleteTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		deleteTakenLecturePort.deleteAllTakenLecturesByUser(user);
	}

	@Override
	public void deleteTakenLecture(Long userId, Long deletedTakenLectureId) {
		User user = findUserUseCase.findUserById(userId);
		deleteTakenLecturePort.deleteTakenLectureById(deletedTakenLectureId);
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);
	}
}
