package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInformationResponse {

	@Schema(name = "studentNumber", example = "60202000")
	private final String studentNumber;
	@Schema(name = "studentName", example = "홍길동")
	private final String studentName;
	@Schema(name = "major", example = "디지털콘텐츠디자인학과")
	private final String major;

	@Builder
	private UserInformationResponse(String studentNumber, String studentName, String major) {
		this.studentNumber = studentNumber;
		this.studentName = studentName;
		this.major = major;
	}
}
