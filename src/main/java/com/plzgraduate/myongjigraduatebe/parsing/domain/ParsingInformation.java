package com.plzgraduate.myongjigraduatebe.parsing.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingInformation {
	private final String studentName;
	private final String studentNumber;
	private final String major;
	private String subMajor;
	private String associatedMajor;
	private StudentCategory studentCategory;
	private List<ParsingTakenLectureDto> takenLectureInformation;

	@Builder
	public ParsingInformation(String studentName, String studentNumber, String major, String subMajor,
		String associatedMajor, StudentCategory studentCategory, List<ParsingTakenLectureDto> takenLectureInformation) {
		this.studentName = studentName;
		this.studentNumber = studentNumber;
		this.major = major;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.takenLectureInformation = takenLectureInformation;
	}

	public static ParsingInformation parsing(String parsingText) {
		String[] splitText = splitParsingText(parsingText);
		ParsingStudentCategoryDto parsingStudentCategoryDto = parseStudentCategory(splitText);
		return ParsingInformation.builder()
			.studentName(parseStudentName(splitText))
			.studentNumber(parseStudentNumber(splitText))
			.major(parseMajor(splitText))
			.subMajor(parsingStudentCategoryDto.getSubMajor())
			.associatedMajor(parsingStudentCategoryDto.getAssociatedMajor())
			.studentCategory(parsingStudentCategoryDto.getStudentCategory())
			.takenLectureInformation(parseTakenLectureInformation(splitText))
			.build();
	}

	private static String[] splitParsingText(String parsingText) {
		return parsingText.split("\\|");
	}

	public static String parseStudentName(String[] splitText) {
		return splitText[2].split(", ")[1].split("\\(")[0];
	}

	public static String parseStudentNumber(String[] splitText) {
		return splitText[2].split(", ")[1].split("[(]")[1].substring(0, 8);
	}

	public static String parseMajor(String[] splitText) {
		String[] detailMajor = splitText[2].split(", ")[0].split(" ");
		return detailMajor[detailMajor.length - 1];
	}

	private static ParsingStudentCategoryDto parseStudentCategory(String[] splitText) {
		String subMajor = null;
		String associatedMajor = null;
		StudentCategory studentCategory;
		String text = splitText[1];
		String[] parts = text.split(", ");
		Set<String> categories = new HashSet<>();
		for (String part : parts) {
			if (part.startsWith("복수전공 - ")) {
				categories.add("복수전공");
				subMajor = part.substring("복수전공 - ".length());
			} else if (part.startsWith("연계전공 - ")) {
				categories.add("연계전공");
				associatedMajor = part.substring("복수전공 - ".length());
			}
		}
		studentCategory = StudentCategory.from(categories);
		return ParsingStudentCategoryDto.of(subMajor, associatedMajor, studentCategory);
	}

	private static List<ParsingTakenLectureDto> parseTakenLectureInformation(String[] splitText) {
		List<ParsingTakenLectureDto> takenLectureInformation = new ArrayList<>();
		for (int i = 16; i < splitText.length; i += 7) {
			if (i + 3 < splitText.length && !Pattern.matches("^[A-Z]+$", splitText[i + 3].substring(0, 1))) {
				return takenLectureInformation;
			}
			int year = Integer.parseInt(splitText[i + 1].split(" ")[0]);
			String semester = splitText[i + 1].split(" ")[1];
			String code = splitText[i + 3];
			char grade = splitText[i + 6].charAt(0);
			if (grade != 'F' && grade != 'N' && grade != 'R') {
				takenLectureInformation.add(ParsingTakenLectureDto.of(code, year, Semester.of(semester)));
			}
			if (i + 7 < splitText.length && Character.isDigit(splitText[i + 7].charAt(0))) {
				i++;
			}
		}
		return takenLectureInformation;
	}
}
