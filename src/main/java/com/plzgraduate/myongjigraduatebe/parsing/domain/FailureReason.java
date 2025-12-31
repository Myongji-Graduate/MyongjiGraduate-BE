package com.plzgraduate.myongjigraduatebe.parsing.domain;

import lombok.Getter;

@Getter
public enum FailureReason {

	EMPTY_PARSING_TEXT("PDF를 인식하지 못했습니다.", "파싱 텍스트가 비어있거나 유효하지 않습니다."),
	PARSING_EXCEPTION("PDF 파싱 중 오류가 발생했습니다.", "PDF에서 정보를 읽어오는데 실패했습니다."),
	INCORRECT_STUDENT_NUMBER("학번이 일치하지 않습니다.", "성적표의 학번과 사용자의 학번이 일치하지 않습니다."),
	UNSUPPORTED_STUDENT_CATEGORY("지원하지 않는 학생 유형입니다.", "현재 시스템에서 지원하지 않는 학생 유형입니다."),
	GRADUATION_CHECK_FAILED("졸업 검사에 실패했습니다.", "졸업 요건 검사 중 오류가 발생했습니다."),
	LECTURE_NOT_FOUND("과목 정보를 찾을 수 없습니다.", "성적표에 포함된 과목 정보를 시스템에서 찾을 수 없습니다."),
	UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다.", "예상치 못한 오류가 발생했습니다.");

	private final String message;
	private final String description;

	FailureReason(String message, String description) {
		this.message = message;
		this.description = description;
	}
}

