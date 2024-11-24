package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingInformation {

	private final String studentName;
	private final String studentNumber;
	private final String major;
	private final String changeMajor;
	private final String subMajor;
	private final String dualMajor;
	private final String associatedMajor;
	private final StudentCategory studentCategory;
	private final TransferCredit transferCredit;
	private final List<ParsingTakenLectureDto> takenLectureInformation;

	@Builder
	public ParsingInformation(
		String studentName,
		String studentNumber,
		String major,
		String changeMajor,
		String subMajor,
		String dualMajor,
		String associatedMajor,
		StudentCategory studentCategory,
		TransferCredit transferCredit,
		List<ParsingTakenLectureDto> takenLectureInformation
	) {
		this.studentName = studentName;
		this.studentNumber = studentNumber;
		this.major = major;
		this.changeMajor = changeMajor;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.transferCredit = transferCredit;
		this.takenLectureInformation = takenLectureInformation;
	}

	public static ParsingInformation parsing(String parsingText) {
		String[] splitText = splitParsingText(parsingText);
		ParsingStudentCategoryDto parsingStudentCategoryDto = parseStudentCategory(splitText);
		return ParsingInformation.builder()
			.studentName(parseStudentName(splitText))
			.studentNumber(parseStudentNumber(splitText))
			.major(parseMajor(splitText))
			.dualMajor(parsingStudentCategoryDto.getDualMajor())
			.changeMajor(parsingStudentCategoryDto.getChangeMajor())
			.subMajor(parsingStudentCategoryDto.getSubMajor())
			.associatedMajor(parsingStudentCategoryDto.getAssociatedMajor())
			.studentCategory(parsingStudentCategoryDto.getStudentCategory())
			.transferCredit(parsingStudentCategoryDto.getTransferCredit())
			.takenLectureInformation(parseTakenLectureInformation(splitText))
			.build();
	}

	private static String[] splitParsingText(String parsingText) {
		return parsingText.split("\\|");
	}

	private static String parseStudentName(String[] splitText) {
		String secondLineText = splitText[2];
		int index = getIndexStudentNameAndNumber(secondLineText);
		return secondLineText.split(", ")[index].split("\\(")[0];
	}

	private static String parseStudentNumber(String[] splitText) {
		String secondLineText = splitText[2];
		int index = getIndexStudentNameAndNumber(secondLineText);
		return secondLineText.split(", ")[index].split("[(]")[1].substring(0, 8);
	}

	private static String parseMajor(String[] splitText) {
		String[] detailMajor = splitText[2].split(", ")[0].split(" ");
		return detailMajor[detailMajor.length - 1];
	}

	private static ParsingStudentCategoryDto parseStudentCategory(String[] splitText) {
		String changeMajor = null;
		String dualMajor = null;
		String subMajor = null;
		String associatedMajor = null;
		StudentCategory studentCategory;
		TransferCredit transferCredit;
		String secondLineText = splitText[2];
		String thirdLineText = splitText[3];
		String fourthLineText = splitText[4];
		List<String> categories = new ArrayList<>();
		String[] parts = secondLineText.split(", ");
		String[] thirdLineParts = thirdLineText.split(", ");

		if (thirdLineParts.length == 4) {
			String part = thirdLineParts[3];
			if (part.startsWith("전과내역 -")) {
				categories.add("전과");
				changeMajor = part.substring("전과내역 - ".length());
			}
		}

		for (String part : parts) {
			if (part.startsWith("부전공 - ")) {
				categories.add("부전공");
				subMajor = part.substring("부전공 - ".length());
			} else if (part.startsWith("복수전공 - ")) {
				categories.add("복수전공");
				dualMajor = part.substring("복수전공 - ".length());
			} else if (part.startsWith("연계전공 - ")) {
				categories.add("연계전공");
				associatedMajor = part.substring("연계전공 - ".length());
			}
		}

		studentCategory = StudentCategory.from(categories);

		String fourthLine = fourthLineText.substring("편입생 인정학점 - ".length());
		transferCredit = TransferCredit.from(Arrays.stream(fourthLine.split(","))
			.map(s -> s.replaceAll("[^0-9]", ""))
			.collect(Collectors.joining("/")));

		return ParsingStudentCategoryDto.of(
			changeMajor, subMajor, dualMajor, associatedMajor, studentCategory, transferCredit
		);
	}

	private static List<ParsingTakenLectureDto> parseTakenLectureInformation(String[] splitText) {
		List<ParsingTakenLectureDto> takenLectureInformation = new ArrayList<>();
		for (int i = 16; i < splitText.length; i += 7) {
			if (i + 3 < splitText.length && !Pattern.matches(
				"^[A-Z]+$", splitText[i + 3].substring(0, 1)
			)) {
				return takenLectureInformation;
			}
			int year = Integer.parseInt(splitText[i + 1].split(" ")[0].substring(0, 4));
			String semester = splitText[i + 1].split(" ")[1];
			String code = splitText[i + 3];
			char grade = splitText[i + 6].charAt(0);
			if (grade != 'F' && grade != 'N' && grade != 'R') {
				takenLectureInformation.add(
					ParsingTakenLectureDto.of(code, year, Semester.of(semester)));
			}
			if (i + 7 < splitText.length && Character.isDigit(splitText[i + 7].charAt(0))) {
				i++;
			}
		}
		return takenLectureInformation;
	}

	private static int getIndexStudentNameAndNumber(String text) {
		String[] parts = text.split(", ");
		int index = 0;
		for (String part : parts) {
			if (part.startsWith("현학적 -")) {
				return index - 1;
			}
			index++;
		}
		return 1;
	}
}
