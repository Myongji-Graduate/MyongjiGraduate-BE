package com.plzgraduate.myongjigraduatebe.takenlecture.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.FindLecturesByLectureCodeUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.SaveTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class SaveTakenLectureFromParsingTextService implements SaveTakenLectureFromParsingTextUseCase {

	private final SaveTakenLecturePort saveTakenLecturePort;
	private final FindLecturesByLectureCodeUseCase findLecturesByLectureCodeUseCase;

	@Override
	public void saveTakenLectures(SaveTakenLectureCommand saveTakenLectureCommand){
		User user = saveTakenLectureCommand.getUser();
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList = saveTakenLectureCommand.getTakenLectureInformationList();
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformationList);
		List<TakenLecture> takenLectures = takenLectureInformationList.stream().map(
				takenLectureInformation -> TakenLecture.of(user, lectureMap.get(takenLectureInformation.getLectureCode()),
					takenLectureInformation.getYear(), takenLectureInformation.getSemester()))
			.collect(Collectors.toList());
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private Map<String, Lecture> makeLectureMapByLectureCodes(List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList) {
		List<String> lectureCodes = takenLectureInformationList.stream()
			.map(SaveTakenLectureCommand.TakenLectureInformation::getLectureCode)
			.collect(Collectors.toList());
		List<Lecture> lectures = findLecturesByLectureCodeUseCase.findLecturesByLectureCodes(lectureCodes);
		return lectures.stream()
			.collect(Collectors.toMap(Lecture::getLectureCode, Function.identity()));
	}
}
