package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.command.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
class ParsingTextService implements ParsingTextUseCase {

	private final LoadUserPort loadUserPort;
	private final LoadLecturePort loadLecturePort;
	private final SaveTakenLecturePort saveTakenLecturePort;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;

	@Override
	public void enrollParsingText(ParsingTextCommand parsingTextCommand) {
		User user = loadUserPort.loadUserById(parsingTextCommand.getUserId());
		String parsingText = parsingTextCommand.getParsingText();

		try {
			deleteTakenLecturesIfAlreadyEnrolled(user);
			ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
			updateUser(user, parsingInformation);
			saveTakenLectures(user, parsingInformation);
			saveParsingTextHistoryPort.saveParsingTextHistory(
				ParsingTextHistory.success(user, parsingText)
			);
		} catch (Exception e) {
			saveParsingTextHistoryPort.saveParsingTextHistory(
				ParsingTextHistory.fail(user, parsingText)
			);
		}
	}

	private void deleteTakenLecturesIfAlreadyEnrolled(User user) {
		deleteTakenLecturePort.deleteTakenLecturesByUser(user);
	}

	private void saveTakenLectures(User user, ParsingInformation parsingInformation) {
		List<ParsingTakenLectureDto> takenLectureInformation = parsingInformation.getTakenLectureInformation();
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformation);
		List<TakenLecture> takenLectures = takenLectureInformation.stream().map(
			parsingTakenLectureDto -> TakenLecture.create(user, lectureMap.get(parsingTakenLectureDto.getLectureCode()),
				parsingTakenLectureDto.getYear(), parsingTakenLectureDto.getSemester()))
			.collect(Collectors.toList());
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private Map<String, Lecture> makeLectureMapByLectureCodes(List<ParsingTakenLectureDto> takenLectureInformation) {
		List<String> lectureCodes = takenLectureInformation.stream()
			.map(ParsingTakenLectureDto::getLectureCode)
			.collect(Collectors.toList());
		List<Lecture> lectures = loadLecturePort.loadLecturesByLectureCodes(lectureCodes);
		return lectures.stream()
			.collect(Collectors.toMap(Lecture::getLectureCode, Function.identity()));
	}

	private void updateUser(User user, ParsingInformation parsingInformation) {
		user.update(parsingInformation.getStudentName(), parsingInformation.getMajor(),
			parsingInformation.getSubMajor(), parsingInformation.getStudentCategory());
	}
}
