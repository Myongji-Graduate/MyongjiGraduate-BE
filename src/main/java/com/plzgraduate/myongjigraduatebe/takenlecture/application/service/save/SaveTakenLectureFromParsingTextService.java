package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.FindLecturesByLectureCodeUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class SaveTakenLectureFromParsingTextService implements SaveTakenLectureFromParsingTextUseCase {

	private final SaveTakenLecturePort saveTakenLecturePort;
	private final FindLecturesByLectureCodeUseCase findLecturesByLectureCodeUseCase;

	@Override
	public void saveTakenLectures(SaveTakenLectureCommand saveTakenLectureCommand){
		User user = saveTakenLectureCommand.getUser();
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList = saveTakenLectureCommand.getTakenLectureInformationList();
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformationList);
		List<TakenLecture> takenLectures = makeTakenLectures(user, takenLectureInformationList, lectureMap);
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private List<TakenLecture> makeTakenLectures(User user,
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList,
		Map<String, Lecture> lectureMap) {
		return takenLectureInformationList.stream().map(
			takenLectureInformation -> {
				Lecture lecture = getLectureFromLectureMap(lectureMap, takenLectureInformation);
				return TakenLecture.of(user, lecture, takenLectureInformation.getYear(), takenLectureInformation.getSemester());
			}
		).collect(Collectors.toList());
	}

	private Lecture getLectureFromLectureMap(Map<String, Lecture> lectureMap,
		SaveTakenLectureCommand.TakenLectureInformation takenLectureInformation) {
		return Optional.ofNullable(lectureMap.get(takenLectureInformation.getLectureCode()))
			.orElseThrow(() -> new IllegalArgumentException(takenLectureInformation.getLectureCode() + "이 데이터베이스에 존재하지 않습니다."));
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
