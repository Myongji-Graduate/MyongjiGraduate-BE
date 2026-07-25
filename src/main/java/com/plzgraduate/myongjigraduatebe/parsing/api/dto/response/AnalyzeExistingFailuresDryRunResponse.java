package com.plzgraduate.myongjigraduatebe.parsing.api.dto.response;

import com.plzgraduate.myongjigraduatebe.parsing.application.service.FailureAnalysisService.FailureAnalysisPreview;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import java.util.Map;
import lombok.Getter;

@Getter
public class AnalyzeExistingFailuresDryRunResponse {

	private final int totalCount;
	private final int passedCount;
	private final Map<FailureReason, Integer> failureCounts;

	private AnalyzeExistingFailuresDryRunResponse(FailureAnalysisPreview preview) {
		this.totalCount = preview.getTotalCount();
		this.passedCount = preview.getPassedCount();
		this.failureCounts = preview.getFailureCounts();
	}

	public static AnalyzeExistingFailuresDryRunResponse from(FailureAnalysisPreview preview) {
		return new AnalyzeExistingFailuresDryRunResponse(preview);
	}
}
