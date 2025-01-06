package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DOUBLE_SUB;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ParsingAnonymousService implements ParsingAnonymousUseCase {

	@Override
	public ParsingAnonymousDto parseAnonymous(
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String parsingText
	) {
		validateParsingText(parsingText);
		ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
		checkUnSupportedUser(parsingInformation);

		User anonymous = User.createAnonymous(
			englishLevel,
			koreanLevel,
			parsingInformation.getStudentName(),
			parsingInformation.getStudentNumber(),
			parsingInformation.getMajor(),
			parsingInformation.getSubMajor(),
			parsingInformation.getDualMajor(),
			parsingInformation.getAssociatedMajor(),
			parsingInformation.getStudentCategory(),
			parsingInformation.getTransferCredit(),
			parsingInformation.getExchangeCredit()
		);

		TakenLectureInventory takenLectureInventory = getTakenLectureInventory(
			anonymous,
			parsingInformation.getTakenLectureInformation()
		);

		return new ParsingAnonymousDto(anonymous, takenLectureInventory);
	}

	private void validateParsingText(String parsingText) {
		if (parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void checkUnSupportedUser(ParsingInformation parsingInformation) {
		if (parsingInformation.getStudentCategory() == DOUBLE_SUB) {
			throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString());
		}
	}

	private TakenLectureInventory getTakenLectureInventory(
		User anonymous,
		List<ParsingTakenLectureDto> parsingTakenLectureDtoList
	) {
		Set<TakenLecture> takenLectures = parsingTakenLectureDtoList.stream()
			.map(parsingTakenLectureDto -> TakenLecture.of(
					anonymous,
					Lecture.from(parsingTakenLectureDto.getLectureCode()),
					parsingTakenLectureDto.getYear(),
					parsingTakenLectureDto.getSemester()
				)
			)
			.collect(Collectors.toSet());

		return TakenLectureInventory.from(takenLectures);
	}
}
