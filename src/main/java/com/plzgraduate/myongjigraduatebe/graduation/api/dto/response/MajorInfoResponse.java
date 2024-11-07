package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;


import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
public class MajorInfoResponse {

	@Schema(name = "primaryMajor", example = "데이터테크놀로지전공")
	private final String primaryMajor;
	@Schema(name = "doubleMajor", example = "융합소프트웨어전공", nullable = true)
	private final String doubleMajor;
	@Schema(name = "subMajor", example = "융합소프트웨어전공", nullable = true)
	private final String subMajor;

	@Builder
	private MajorInfoResponse(String primaryMajor, String doubleMajor, String subMajor) {
		this.primaryMajor = primaryMajor;
		this.doubleMajor = doubleMajor;
		this.subMajor = subMajor;
	}

	public static MajorInfoResponse from(User user) {
		return MajorInfoResponse.builder()
			.primaryMajor(user.getPrimaryMajor())
			.doubleMajor(null) //TODO: 복수전공 추가
			.subMajor(user.getSubMajor())
			.build();
	}

}
