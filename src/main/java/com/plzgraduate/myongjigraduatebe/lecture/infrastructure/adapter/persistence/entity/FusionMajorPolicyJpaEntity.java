package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fusion_major_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FusionMajorPolicyJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100)
	private String fusionMajor;
	private int requiredCredit;
	private Integer businessRequiredCredit;
	private int basicCredit;
	private Integer businessBasicCredit;
	private int primaryMajorCredit;
	private Integer businessPrimaryMajorCredit;
	private int startEntryYear;
	private int endEntryYear;
}
