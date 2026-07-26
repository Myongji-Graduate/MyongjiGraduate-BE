package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fusion_major_lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FusionMajorLectureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "policy_id")
	private FusionMajorPolicyJpaEntity policy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id")
	private LectureJpaEntity lecture;

	@Column(length = 100)
	private String equivalenceKey;

	@Column(length = 30)
	private String requirementArea;

	private int mandatory;
}
