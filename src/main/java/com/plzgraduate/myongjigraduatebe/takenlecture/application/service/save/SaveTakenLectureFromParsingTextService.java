package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Transactional
@RequiredArgsConstructor
@Slf4j
class SaveTakenLectureFromParsingTextService implements SaveTakenLectureFromParsingTextUseCase {

	private final SaveTakenLecturePort saveTakenLecturePort;
	private final FindLecturesUseCase findLecturesUseCase;

	@Override
	public void saveTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList) {
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformationList);
		List<TakenLecture> takenLectures = makeTakenLectures(user, takenLectureInformationList, lectureMap);
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private List<TakenLecture> makeTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList,
		Map<String, Lecture> lectureMap) {
		return takenLectureInformationList.stream()
			.map(takenLectureInformation -> {
					Lecture lecture = getLectureFromLectureMap(lectureMap, takenLectureInformation);
					return TakenLecture.of(user, lecture, takenLectureInformation.getYear(),
						takenLectureInformation.getSemester());
				}
			).collect(Collectors.toList());
	}

	private Lecture getLectureFromLectureMap(Map<String, Lecture> lectureMap,
		TakenLectureInformation takenLectureInformation) {
		return Optional.ofNullable(lectureMap.get(takenLectureInformation.getLectureCode()))
			.orElseThrow(() -> {
				log.warn("Not Found Lecture in Database: {}", takenLectureInformation.getLectureCode());
				return new IllegalArgumentException(ErrorCode.NON_EXISTED_LECTURE.toString());
			});
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
