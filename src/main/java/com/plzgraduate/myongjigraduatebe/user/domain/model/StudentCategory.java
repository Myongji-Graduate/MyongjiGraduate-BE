package com.plzgraduate.myongjigraduatebe.user.domain.model;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

@Getter
@RequiredArgsConstructor
public enum StudentCategory {
	NORMAL(
		List.of(),
		List.of(COMMON_CULTURE, CORE_CULTURE, PRIMARY_BASIC_ACADEMICAL_CULTURE,
			PRIMARY_MANDATORY_MAJOR,
			PRIMARY_ELECTIVE_MAJOR, NORMAL_CULTURE, FREE_ELECTIVE, CHAPEL
		)
	),
	CHANGE_MAJOR(
		List.of("전과"),
		List.of(COMMON_CULTURE, CORE_CULTURE, PRIMARY_BASIC_ACADEMICAL_CULTURE,
			PRIMARY_MANDATORY_MAJOR,
			PRIMARY_ELECTIVE_MAJOR, NORMAL_CULTURE, FREE_ELECTIVE, CHAPEL
		)
	),
	SUB_MAJOR(
		List.of("부전공"),
		List.of(COMMON_CULTURE, CORE_CULTURE, PRIMARY_BASIC_ACADEMICAL_CULTURE,
			PRIMARY_MANDATORY_MAJOR,
			PRIMARY_ELECTIVE_MAJOR, GraduationCategory.SUB_MAJOR, NORMAL_CULTURE, FREE_ELECTIVE,
			CHAPEL
		)
	),
	DUAL_MAJOR(
		List.of("복수전공"),
		List.of(COMMON_CULTURE, CORE_CULTURE, PRIMARY_BASIC_ACADEMICAL_CULTURE,
			DUAL_BASIC_ACADEMICAL_CULTURE,
			PRIMARY_MANDATORY_MAJOR, PRIMARY_ELECTIVE_MAJOR, DUAL_MANDATORY_MAJOR,
			DUAL_ELECTIVE_MAJOR, NORMAL_CULTURE,
			FREE_ELECTIVE, CHAPEL
		)
	),
	ASSOCIATED_MAJOR(
		List.of("연계전공"),
		List.of()
	),
	DOUBLE_SUB(
		List.of("복수전공", "부전공"),
		// 현재 미지원
		List.of(COMMON_CULTURE, CORE_CULTURE, PRIMARY_BASIC_ACADEMICAL_CULTURE,
			PRIMARY_MANDATORY_MAJOR,
			PRIMARY_ELECTIVE_MAJOR, NORMAL_CULTURE, FREE_ELECTIVE, CHAPEL
		)
	),
	TRANSFER(
		List.of("편입"),
		List.of(
			PRIMARY_MANDATORY_MAJOR,
			PRIMARY_ELECTIVE_MAJOR,
			TRANSFER_CHRISTIAN,
			NORMAL_CULTURE,
			FREE_ELECTIVE,
			CHAPEL
		)
	);

	private final List<String> categories;
	private final List<GraduationCategory> includedGraduationCategories;

	public static StudentCategory from(List<String> categories) {
		return Arrays.stream(StudentCategory.values())
			.filter(studentCategory -> Objects.equals(studentCategory.getCategories(), categories))
			.findFirst()
			.orElseThrow(() -> new PdfParsingException("PDF 정보 추출에 실패했습니다."));
	}

	public void validateGraduationCategoryInclusion(GraduationCategory graduationCategory) {
		if (!includedGraduationCategories.contains(graduationCategory)) {
			throw new IllegalArgumentException(ErrorCode.UNFITTED_GRADUATION_CATEGORY.toString());
		}
	}
}
