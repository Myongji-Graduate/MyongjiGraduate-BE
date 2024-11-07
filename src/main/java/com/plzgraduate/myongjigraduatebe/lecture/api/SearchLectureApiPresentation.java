package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.SearchLectureResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SearchLecture", description = "type과 keyword를 통해 과목정보를 검색하는 API")
public interface SearchLectureApiPresentation {

	List<SearchLectureResponse> searchLecture(
		@LoginUser Long userId,
		@Parameter(name = "type", description = "과목명 또는 과목코드") @RequestParam(defaultValue = "name") String type,
		@Parameter(name = "keyword", description = "검색어 2자리 이상") @RequestParam @Size(min = 2, message = "검색어를 2자리 이상 입력해주세요.") String keyword
	);
}
