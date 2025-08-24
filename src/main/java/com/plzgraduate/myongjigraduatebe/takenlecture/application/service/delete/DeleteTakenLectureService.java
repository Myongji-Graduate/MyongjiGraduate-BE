package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.parsing.api.TakenLectureCacheEvict;
import java.util.List;
import java.util.HashSet;

@UseCase
@Transactional
@RequiredArgsConstructor
public class DeleteTakenLectureService implements DeleteTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindTakenLecturePort findTakenLecturePort;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;
	private final TakenLectureCacheEvict takenLectureCacheEvict;

	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		deleteTakenLecturePort.deleteAllTakenLecturesByUser(user);
	}

	@Override
	public void deleteTakenLecture(final Long userId, final Long takenLectureId) {
		User user = findUserUseCase.findUserById(userId);
		
		// 삭제할 과목 정보 찾기
		List<TakenLecture> beforeTakenLectures = findTakenLecturePort.findTakenLecturesByUser(user);
		TakenLectureInventory beforeTakenLectureInventory = TakenLectureInventory.from(new HashSet<>(beforeTakenLectures));
		
		TakenLecture takenLectureToDelete = beforeTakenLectureInventory.getTakenLectures().stream()
			.filter(taken -> taken.getId().equals(takenLectureId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("삭제할 과목을 찾을 수 없습니다: " + takenLectureId));
		
		deleteTakenLecturePort.deleteTakenLectureById(takenLectureId);
		
		// 캐시 무효화 (학점 재계산 전에 수행)
		takenLectureCacheEvict.evictTakenLecturesCache(userId);
		
		// 학점 재계산
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);
	}
}
