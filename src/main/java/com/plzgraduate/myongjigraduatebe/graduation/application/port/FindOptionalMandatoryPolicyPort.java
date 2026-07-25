package com.plzgraduate.myongjigraduatebe.graduation.application.port;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import java.util.List;

public interface FindOptionalMandatoryPolicyPort {

	List<OptionalMandatoryPolicy> findActivePolicies(
		String major, int entryYear, MajorType majorType);

	default List<OptionalMandatoryPolicy> findActiveBasicPolicies(String major, int entryYear) {
		return List.of();
	}
}
