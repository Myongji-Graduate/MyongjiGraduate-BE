package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
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
	public void enrollParsingText(ParsingTextCommand parsingTextCommand) {
		String parsingText = parsingTextCommand.getParsingText();
		User user = findUserUseCase.findUserById(parsingTextCommand.getUserId());
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

	private void saveTakenLectures(User user, ParsingInformation parsingInformation){
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList = parsingInformation.getTakenLectureInformation();
		SaveTakenLectureCommand saveTakenLectureCommand = getSaveTakenLectureCommand(
			user, parsingTakenLectureDtoList);
		saveTakenLectureFromParsingTextUseCase.saveTakenLectures(saveTakenLectureCommand);
	}

	private void updateUser(User user, ParsingInformation parsingInformation) {
		UpdateStudentInformationCommand updateStudentInfoCommand = UpdateStudentInformationCommand.of(user,
			parsingInformation.getStudentName(), parsingInformation.getMajor(),
			parsingInformation.getAssociatedMajor(), parsingInformation.getSubMajor(), parsingInformation.getStudentCategory());
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

	private SaveTakenLectureCommand getSaveTakenLectureCommand(User user,
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList) {
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList = parsingTakenLectureDtoList.stream()
			.map(parsingTakenLectureDto ->
				SaveTakenLectureCommand.createTakenLectureInformation(
					parsingTakenLectureDto.getLectureCode(),
					parsingTakenLectureDto.getYear(),
					parsingTakenLectureDto.getSemester())
			)
			.collect(Collectors.toList());
		return SaveTakenLectureCommand.of(user, takenLectureInformationList);
	}

	private void checkIfNormal(ParsingInformation parsingInformation) {
		if(parsingInformation.getStudentCategory() != StudentCategory.NORMAL) {
			throw new IllegalArgumentException("전과생, 복수전공, 연계전공은 참여가 어렵습니다. 빠른 시일 내에 업데이트하도록 하겠습니다.");
		}
	}

}
