package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingTextHistory {

	private final Long id;
	private final User user;
	private final String parsingText;
	private final ParsingResult parsingResult;
	private final FailureReason failureReason;
	private final String failureDetails;
	private final String trackingCode;
	private final ParsingRequesterType requesterType;

	@Builder
	private ParsingTextHistory(
		Long id,
		User user,
		String parsingText,
		ParsingResult parsingResult,
		FailureReason failureReason,
		String failureDetails,
		String trackingCode,
		ParsingRequesterType requesterType
	) {
		this.id = id;
		this.user = user;
		this.parsingText = parsingText;
		this.parsingResult = parsingResult;
		this.failureReason = failureReason;
		this.failureDetails = failureDetails;
		this.trackingCode = trackingCode;
		this.requesterType = requesterType;
	}

	public static ParsingTextHistory success(User user, String parsingText) {
		return ParsingTextHistory.builder()
			.user(user)
			.parsingText(parsingText)
			.parsingResult(ParsingResult.SUCCESS)
			.requesterType(ParsingRequesterType.MEMBER)
			.build();
	}

	public static ParsingTextHistory fail(User user, String parsingText) {
		return ParsingTextHistory.builder()
			.user(user)
			.parsingText(parsingText)
			.parsingResult(ParsingResult.FAIL)
			.requesterType(ParsingRequesterType.MEMBER)
			.build();
	}

	public static ParsingTextHistory fail(User user, String parsingText, FailureReason failureReason, String failureDetails) {
		return ParsingTextHistory.builder()
			.user(user)
			.parsingText(parsingText)
			.parsingResult(ParsingResult.FAIL)
			.failureReason(failureReason)
			.failureDetails(failureDetails)
			.requesterType(ParsingRequesterType.MEMBER)
			.build();
	}

	public static ParsingTextHistory anonymousSuccess(String parsingText, String trackingCode) {
		return ParsingTextHistory.builder()
			.parsingText(parsingText)
			.parsingResult(ParsingResult.SUCCESS)
			.trackingCode(trackingCode)
			.requesterType(ParsingRequesterType.ANONYMOUS)
			.build();
	}

	public static ParsingTextHistory anonymousFail(
		String parsingText,
		FailureReason failureReason,
		String failureDetails,
		String trackingCode
	) {
		return ParsingTextHistory.builder()
			.parsingText(parsingText)
			.parsingResult(ParsingResult.FAIL)
			.failureReason(failureReason)
			.failureDetails(failureDetails)
			.trackingCode(trackingCode)
			.requesterType(ParsingRequesterType.ANONYMOUS)
			.build();
	}
}
