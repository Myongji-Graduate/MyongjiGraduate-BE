package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class SaveTakenLectureFromParsingTextService implements SaveTakenLectureFromParsingTextUseCase {

	private final SaveTakenLecturePort saveTakenLecturePort;
	private final FindLecturesUseCase findLecturesUseCase;
	private final CalculateGraduationUseCase calculateGraduationUseCase;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@Override
	public void saveTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList) {
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformationList);
		List<TakenLecture> takenLectures = makeTakenLectures(user, takenLectureInformationList, lectureMap);
		saveTakenLecturePort.saveTakenLectures(takenLectures);

		GraduationResult graduationResult = calculateGraduationUseCase.calculateGraduation(user,
			new HashSet<>(takenLectures));
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user, graduationResult);
	}

	private List<TakenLecture> makeTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList,
		Map<String, Lecture> lectureMap) {
		return takenLectureInformationList.stream().map(
			takenLectureInformation -> {
				Lecture lecture = getLectureFromLectureMap(lectureMap, takenLectureInformation);
				return TakenLecture.of(user, lecture, takenLectureInformation.getYear(),
					takenLectureInformation.getSemester());
			}
		).collect(Collectors.toList());
	}

	private Lecture getLectureFromLectureMap(Map<String, Lecture> lectureMap,
		TakenLectureInformation takenLectureInformation) {
		return Optional.ofNullable(lectureMap.get(takenLectureInformation.getLectureCode()))
			.orElseThrow(
				() -> new IllegalArgumentException(takenLectureInformation.getLectureCode() + "이 데이터베이스에 존재하지 않습니다."));
	}

	private Map<String, Lecture> makeLectureMapByLectureCodes(
		List<TakenLectureInformation> takenLectureInformationList) {
		List<String> lectureCodes = takenLectureInformationList.stream()
			.map(TakenLectureInformation::getLectureCode)
			.collect(Collectors.toList());
		List<Lecture> lectures = findLecturesUseCase.findLecturesByLectureCodes(lectureCodes);
		return lectures.stream()
			.collect(Collectors.toMap(Lecture::getLectureCode, Function.identity()));
	}
}
