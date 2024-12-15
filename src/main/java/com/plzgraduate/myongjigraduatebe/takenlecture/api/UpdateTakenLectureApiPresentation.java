package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateCustomizedTakenLectureRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UpdateTakenLecture", description = "수강과목을 수정하는 API")
public interface UpdateTakenLectureApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	void generateCustomizedTakenLecture(
		@LoginUser Long userId,
		@Valid @RequestBody GenerateCustomizedTakenLectureRequest generateCustomizedTakenLectureRequest
	);

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@Parameter(name = "takenLectureId", description = "삭제할 수강 과목 ID")
	void deleteCustomizedTakenLecture(
		@LoginUser Long userId,
		@Valid @PathVariable Long takenLectureId
	);
}
