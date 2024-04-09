package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentCategory {
	NORMAL(List.of()),
	CHANGE_MAJOR(List.of("전과")),
	SUB_MAJOR(List.of("부전공")),
	DUAL_MAJOR(List.of("복수전공")),
	ASSOCIATED_MAJOR(List.of("연계전공")),
	DOUBLE_SUB(List.of("복수전공", "부전공"));

	private final List<String> categories;

	public static StudentCategory from(List<String> categories) {
		return Arrays.stream(StudentCategory.values())
			.filter(studentCategory -> Objects.equals(studentCategory.getCategories(), categories))
			.findFirst()
			.orElseThrow(() -> new PdfParsingException("PDF 정보 추출에 실패했습니다."));
	}

}
