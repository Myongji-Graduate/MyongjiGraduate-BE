package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import jakarta.persistence.Entity;
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
@Table(name = "optional_mandatory_policy_lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionalMandatoryPolicyLectureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "policy_id", nullable = false)
	private OptionalMandatoryPolicyJpaEntity policy;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "lecture_id", nullable = false)
	private LectureJpaEntity lecture;

	private String equivalenceKey;
}
