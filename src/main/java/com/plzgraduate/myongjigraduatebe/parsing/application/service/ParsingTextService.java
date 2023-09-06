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
import com.plzgraduate.myongjigraduatebe.user.application.port.out.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
class ParsingTextService implements ParsingTextUseCase {

	private final ParsingTextHistoryService parsingTextHistoryService;
	private final LoadUserPort loadUserPort;
	private final UpdateUserPort updateUserPort;
	private final LoadLecturePort loadLecturePort;
	private final SaveTakenLecturePort saveTakenLecturePort;
	private final DeleteTakenLecturePort deleteTakenLecturePort;

	@Override
	public void enrollParsingText(ParsingTextCommand parsingTextCommand) {
		String parsingText = parsingTextCommand.getParsingText();
		User user = loadUserPort.loadUserById(parsingTextCommand.getUserId());
		try {
			validateParsingText(parsingText);
			ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
			validateStudentNumber(user, parsingInformation);
			updateUser(user, parsingInformation);
			deleteTakenLecturesIfAlreadyEnrolled(user);
			saveTakenLectures(user, parsingInformation);
			parsingTextHistoryService.saveParsingTextHistory(ParsingTextHistory.success(user, parsingText));
		} catch (InvalidPdfException e) {
			parsingTextHistoryService.saveParsingTextHistory(ParsingTextHistory.fail(user, parsingText));
			throw e;
		} catch (Exception e) {
			parsingTextHistoryService.saveParsingTextHistory(ParsingTextHistory.fail(user, parsingText));
			throw new PdfParsingException("PDF에서 정보를 읽어오는데 실패했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void deleteTakenLecturesIfAlreadyEnrolled(User user) {
		deleteTakenLecturePort.deleteAllTakenLecturesByUser(user);
	}

	private void saveTakenLectures(User user, ParsingInformation parsingInformation){
		List<ParsingTakenLectureDto> takenLectureInformation = parsingInformation.getTakenLectureInformation();
		Map<String, Lecture> lectureMap = makeLectureMapByLectureCodes(takenLectureInformation);
		List<TakenLecture> takenLectures = takenLectureInformation.stream().map(
			parsingTakenLectureDto -> TakenLecture.of(user, lectureMap.get(parsingTakenLectureDto.getLectureCode()),
				parsingTakenLectureDto.getYear(), parsingTakenLectureDto.getSemester()))
			.collect(Collectors.toList());
		saveTakenLecturePort.saveTakenLectures(takenLectures);
	}

	private Map<String, Lecture> makeLectureMapByLectureCodes(List<ParsingTakenLectureDto> takenLectureInformation) {
		List<String> lectureCodes = takenLectureInformation.stream()
			.map(ParsingTakenLectureDto::getLectureCode)
			.collect(Collectors.toList());
		List<Lecture> lectures = loadLecturePort.loadLecturesByLectureCodes(lectureCodes);
		System.out.println(lectures.size());
		return lectures.stream()
			.collect(Collectors.toMap(Lecture::getLectureCode, Function.identity()));
	}

	private void updateUser(User user, ParsingInformation parsingInformation) {
		user.update(parsingInformation.getStudentName(), parsingInformation.getMajor(),
			parsingInformation.getSubMajor(), parsingInformation.getStudentCategory());
		updateUserPort.updateUser(user);
	}

	private void validateParsingText(String parsingText) {
		if (parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다.");
		}
	}

	private void validateStudentNumber(User user, ParsingInformation parsingInformation) {
		if (!user.getStudentNumber().equals(parsingInformation.getStudentNumber())) {
			throw new InvalidPdfException("본인의 PDF 학번이 일치하지 않습니다.");
		}
	}

}
