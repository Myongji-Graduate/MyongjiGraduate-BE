package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.log.application.usecase.LogInvalidLectureUseCase;
import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;

import java.time.LocalDateTime;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
@Slf4j
class SaveTakenLectureFromParsingTextService implements SaveTakenLectureFromParsingTextUseCase {
	private final SaveTakenLecturePort saveTakenLecturePort;
	private final FindLecturesUseCase findLecturesUseCase;
	private final LogInvalidLectureUseCase logInvalidLectureUseCase;

	@Override
	public void saveTakenLectures(User user,
		List<TakenLectureInformation> takenLectureInformationList) {
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformationList);
		List<TakenLecture> takenLectures = makeTakenLectures(user, takenLectureInformationList,
			lectureMap);
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private List<TakenLecture> makeTakenLectures(User user,
		List<TakenLectureInformation> takenLectureInformationList,
		Map<String, Lecture> lectureMap) {

		List<TakenLecture> result = new ArrayList<>();
		List<String> invalidLectureCodes = new ArrayList<>();

		for (TakenLectureInformation info : takenLectureInformationList) {
			Lecture lecture = lectureMap.get(info.getLectureCode());

			if (lecture == null) {
				log.warn("Not Found Lecture in Database: {}", info.getLectureCode());
				logInvalidLectureUseCase.log(
					InvalidTakenLectureLog.builder()
						.studentNumber(user.getStudentNumber())
						.lectureCode(info.getLectureCode())
						.lectureName(info.getLectureName())
						.year(info.getYear())
						.semester(info.getSemester().getValue())
						.timestamp(LocalDateTime.now())
						.build()
				);
				invalidLectureCodes.add(info.getLectureCode());
				continue;
			}

			result.add(TakenLecture.of(user, lecture, info.getYear(), info.getSemester()));
		}

		if (!invalidLectureCodes.isEmpty()) {
			throw new IllegalArgumentException(ErrorCode.NON_EXISTED_LECTURE.toString());
		}

		return result;
	}


	private Map<String, Lecture> makeLectureMapByLectureCodes(
		List<TakenLectureInformation> takenLectureInformationList) {
		List<String> lectureCodes = takenLectureInformationList.stream()
			.map(TakenLectureInformation::getLectureCode)
			.collect(Collectors.toList());
		List<Lecture> lectures = findLecturesUseCase.findLecturesByLectureCodes(lectureCodes);
		return lectures.stream()
			.collect(Collectors.toMap(Lecture::getId, Function.identity()));
	}
}
