package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "optional_mandatory_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionalMandatoryPolicyJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String major;
	private int requiredCount;
	private int requiredCredit;
	private int startEntryYear;
	private int endEntryYear;
	private int policyVersion;

	@Enumerated(EnumType.STRING)
	private MajorType majorType;

	@Enumerated(EnumType.STRING)
	private PolicyStatus status;

	@OneToMany(mappedBy = "policy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OptionalMandatoryPolicyLectureJpaEntity> lectures = new ArrayList<>();

	public enum PolicyStatus {
		DRAFT, ACTIVE, RETIRED
	}
}
