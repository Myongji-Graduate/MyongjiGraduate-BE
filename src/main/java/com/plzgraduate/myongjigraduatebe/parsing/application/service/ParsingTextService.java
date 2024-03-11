package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.CHANGE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.NORMAL;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.SUB_MAJOR;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
class ParsingTextService implements ParsingTextUseCase {

	private final FindUserUseCase findUserUseCase;
	private final UpdateStudentInformationUseCase updateStudentInformationUseCase;
	private final SaveTakenLectureFromParsingTextUseCase saveTakenLectureFromParsingTextUseCase;
	private final DeleteTakenLectureByUserUseCase deleteTakenLectureByUserUseCase;

	@Override
	public void enrollParsingText(Long userId, String parsingText) {
		User user = findUserUseCase.findUserById(userId);
		try {
			validateParsingText(parsingText);
			ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
			checkIfNormal(parsingInformation);
			validateStudentNumber(user, parsingInformation);
			updateUser(user, parsingInformation);
			deleteTakenLecturesIfAlreadyEnrolled(user);
			saveTakenLectures(user, parsingInformation);
		} catch (InvalidPdfException | IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new PdfParsingException("PDF에서 정보를 읽어오는데 실패했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void deleteTakenLecturesIfAlreadyEnrolled(User user) {
		deleteTakenLectureByUserUseCase.deleteAllTakenLecturesByUser(user);
	}

	private void saveTakenLectures(User user, ParsingInformation parsingInformation) {
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList = parsingInformation.getTakenLectureInformation();
		List<TakenLectureInformation> saveTakenLectureCommand = getSaveTakenLectureCommand(user,
			parsingTakenLectureDtoList);
		saveTakenLectureFromParsingTextUseCase.saveTakenLectures(user, saveTakenLectureCommand);
	}

	private void updateUser(User user, ParsingInformation parsingInformation) {
		UpdateStudentInformationCommand updateStudentInfoCommand = UpdateStudentInformationCommand.of(user,
			parsingInformation.getStudentName(), parsingInformation.getMajor(),
			parsingInformation.getAssociatedMajor(), parsingInformation.getSubMajor(),
			parsingInformation.getStudentCategory());
		updateStudentInformationUseCase.updateUser(updateStudentInfoCommand);
	}

	private void validateParsingText(String parsingText) {
		if (parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void validateStudentNumber(User user, ParsingInformation parsingInformation) {
		if (!user.compareStudentNumber(parsingInformation.getStudentNumber())) {
			throw new InvalidPdfException("본인의 학번과 PDF 학번이 일치하지 않습니다.");
		}
	}

	private List<TakenLectureInformation> getSaveTakenLectureCommand(User user,
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList) {
		return parsingTakenLectureDtoList.stream()
			.map(parsingTakenLectureDto ->
				TakenLectureInformation.createTakenLectureInformation(
					parsingTakenLectureDto.getLectureCode(),
					parsingTakenLectureDto.getYear(),
					parsingTakenLectureDto.getSemester())
			)
			.collect(Collectors.toList());
	}

	private void checkIfNormal(ParsingInformation parsingInformation) {
		//TODO: 복수전공 파싱 통과 추가
		if (!(parsingInformation.getStudentCategory() == NORMAL
			|| parsingInformation.getStudentCategory() == CHANGE_MAJOR
			|| parsingInformation.getStudentCategory() == SUB_MAJOR)) {
			throw new IllegalArgumentException("복수전공, 연계전공은 참여가 어렵습니다. 빠른 시일 내에 업데이트하도록 하겠습니다.");
		}
	}

}
