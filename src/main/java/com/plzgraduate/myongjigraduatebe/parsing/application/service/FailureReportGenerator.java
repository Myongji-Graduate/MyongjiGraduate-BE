package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.QueryParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class FailureReportGenerator {

	private final QueryParsingTextHistoryPort queryParsingTextHistoryPort;

	/**
	 * 실패 원인별 통계 리포트를 생성합니다.
	 *
	 * @return 실패 원인별 통계 리포트
	 */
	public FailureStatisticsReport generateFailureStatisticsReport() {
		List<ParsingTextHistory> failures = queryParsingTextHistoryPort.findByParsingResult(ParsingResult.FAIL);

		Map<FailureReason, Long> failureReasonCounts = failures.stream()
			.filter(history -> history.getFailureReason() != null)
			.collect(Collectors.groupingBy(
				ParsingTextHistory::getFailureReason,
				Collectors.counting()
			));

		long totalFailures = failures.size();
		long analyzedFailures = failures.stream()
			.filter(history -> history.getFailureReason() != null)
			.count();
		long unanalyzedFailures = totalFailures - analyzedFailures;

		return new FailureStatisticsReport(
			totalFailures,
			analyzedFailures,
			unanalyzedFailures,
			failureReasonCounts,
			LocalDateTime.now()
		);
	}

	/**
	 * 특정 실패 원인에 대한 상세 리포트를 생성합니다.
	 *
	 * @param failureReason 분석할 실패 원인
	 * @return 상세 리포트
	 */
	public FailureDetailReport generateFailureDetailReport(FailureReason failureReason) {
		List<ParsingTextHistory> failures = queryParsingTextHistoryPort.findByParsingResult(ParsingResult.FAIL);

		List<ParsingTextHistory> filteredFailures = failures.stream()
			.filter(history -> history.getFailureReason() == failureReason)
			.collect(Collectors.toList());

		return new FailureDetailReport(
			failureReason,
			filteredFailures.size(),
			filteredFailures,
			LocalDateTime.now()
		);
	}

	/**
	 * 실패 원인별 통계 리포트
	 */
	@Getter
	public static class FailureStatisticsReport {
		private final long totalFailures;
		private final long analyzedFailures;
		private final long unanalyzedFailures;
		private final Map<FailureReason, Long> failureReasonCounts;
		private final LocalDateTime generatedAt;

		public FailureStatisticsReport(
			long totalFailures,
			long analyzedFailures,
			long unanalyzedFailures,
			Map<FailureReason, Long> failureReasonCounts,
			LocalDateTime generatedAt
		) {
			this.totalFailures = totalFailures;
			this.analyzedFailures = analyzedFailures;
			this.unanalyzedFailures = unanalyzedFailures;
			this.failureReasonCounts = new HashMap<>(failureReasonCounts);
			this.generatedAt = generatedAt;
		}

		/**
		 * 리포트를 문자열로 변환합니다.
		 */
		public String toFormattedString() {
			StringBuilder sb = new StringBuilder();
			sb.append("=== 실패 원인 통계 리포트 ===\n");
			sb.append("생성 시간: ").append(generatedAt).append("\n");
			sb.append("총 실패 건수: ").append(totalFailures).append("\n");
			sb.append("분석 완료: ").append(analyzedFailures).append("\n");
			sb.append("분석 미완료: ").append(unanalyzedFailures).append("\n\n");

			sb.append("=== 실패 원인별 분포 ===\n");
			failureReasonCounts.entrySet().stream()
				.sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
				.forEach(entry -> {
					FailureReason reason = entry.getKey();
					Long count = entry.getValue();
					double percentage = totalFailures > 0 ? (count * 100.0 / totalFailures) : 0.0;
					sb.append(String.format("%s: %d건 (%.2f%%)\n", reason.getMessage(), count, percentage));
				});

			return sb.toString();
		}
	}

	/**
	 * 실패 원인별 상세 리포트
	 */
	@Getter
	public static class FailureDetailReport {
		private final FailureReason failureReason;
		private final long count;
		private final List<ParsingTextHistory> failures;
		private final LocalDateTime generatedAt;

		public FailureDetailReport(
			FailureReason failureReason,
			long count,
			List<ParsingTextHistory> failures,
			LocalDateTime generatedAt
		) {
			this.failureReason = failureReason;
			this.count = count;
			this.failures = failures;
			this.generatedAt = generatedAt;
		}

		/**
		 * 리포트를 문자열로 변환합니다.
		 */
		public String toFormattedString() {
			StringBuilder sb = new StringBuilder();
			sb.append("=== 실패 원인 상세 리포트 ===\n");
			sb.append("생성 시간: ").append(generatedAt).append("\n");
			sb.append("실패 원인: ").append(failureReason.getMessage()).append("\n");
			sb.append("설명: ").append(failureReason.getDescription()).append("\n");
			sb.append("총 건수: ").append(count).append("\n\n");

			sb.append("=== 실패 내역 ===\n");
			failures.forEach(history -> {
				sb.append(String.format("ID: %d, 사용자 ID: %d\n",
					history.getId(),
					history.getUser().getId()));
				if (history.getFailureDetails() != null) {
					sb.append("상세: ").append(history.getFailureDetails()).append("\n");
				}
				sb.append("---\n");
			});

			return sb.toString();
		}
	}
}

