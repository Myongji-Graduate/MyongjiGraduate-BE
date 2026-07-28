package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INCORRECT_STUDENT_NUMBER;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DOUBLE_SUB;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.HonorsCollegeMajorPolicy;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional
class ParsingTextService implements ParsingTextUseCase {

	private final FindUserUseCase findUserUseCase;
	private final UpdateStudentInformationUseCase updateStudentInformationUseCase;
	private final SaveTakenLectureFromParsingTextUseCase saveTakenLectureFromParsingTextUseCase;
	private final DeleteTakenLectureUseCase deleteTakenLectureByUserUseCase;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;
	@Override
	public void enrollParsingText(Long userId, String parsingText, boolean honorsCollege, String honorsTargetMajor) {
		User user = findUserUseCase.findUserById(userId);
		try {
			validateParsingText(parsingText);
			ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
			checkUnSupportedUser(parsingInformation);
			validateHonorsTargetMajor(honorsCollege, honorsTargetMajor);
			validateStudentNumber(user, parsingInformation);
			User updatedUser = updateUser(user, parsingInformation, honorsCollege, honorsTargetMajor);
			deleteTakenLecturesIfAlreadyEnrolled(updatedUser);
			saveTakenLectures(updatedUser, parsingInformation);
			generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(updatedUser);
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
		List<TakenLectureInformation> saveTakenLectureCommand = getSaveTakenLectureCommand(
			parsingTakenLectureDtoList);
		saveTakenLectureFromParsingTextUseCase.saveTakenLectures(user, saveTakenLectureCommand);
	}

	private User updateUser(User user, ParsingInformation parsingInformation, boolean honorsCollege,
		String honorsTargetMajor) {
		UpdateStudentInformationCommand updateStudentInfoCommand = UpdateStudentInformationCommand.of(
			user, parsingInformation, honorsCollege, honorsTargetMajor);
		return updateStudentInformationUseCase.updateUser(updateStudentInfoCommand);
	}

	private void validateParsingText(String parsingText) {
		if (parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void validateStudentNumber(User user, ParsingInformation parsingInformation) {
		if (!user.compareStudentNumber(parsingInformation.getStudentNumber())) {
			throw new InvalidPdfException(INCORRECT_STUDENT_NUMBER.toString());
		}
	}

	private List<TakenLectureInformation> getSaveTakenLectureCommand(
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList
	) {
		return parsingTakenLectureDtoList.stream()
			.map(parsingTakenLectureDto ->
				TakenLectureInformation.createTakenLectureInformation(
					parsingTakenLectureDto.getLectureCode(),
					parsingTakenLectureDto.getYear(),
					parsingTakenLectureDto.getSemester(),
					parsingTakenLectureDto.getLectureName()
				)
			)
			.collect(Collectors.toList());
	}

	private void checkUnSupportedUser(ParsingInformation parsingInformation) {
		if (parsingInformation.getStudentCategory() == DOUBLE_SUB) {
			throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString());
		}
	}

	private void validateHonorsTargetMajor(boolean honorsCollege, String honorsTargetMajor) {
		if (!honorsCollege) {
			return;
		}
		if (honorsTargetMajor == null || honorsTargetMajor.isBlank()) {
			throw new IllegalArgumentException("아너칼리지 졸업요건 기준 학과를 선택해 주세요.");
		}
		if (!HonorsCollegeMajorPolicy.isSupportedTargetMajor(honorsTargetMajor)) {
			throw new IllegalArgumentException("선택할 수 없는 아너칼리지 졸업요건 기준 학과입니다.");
		}
	}
}
