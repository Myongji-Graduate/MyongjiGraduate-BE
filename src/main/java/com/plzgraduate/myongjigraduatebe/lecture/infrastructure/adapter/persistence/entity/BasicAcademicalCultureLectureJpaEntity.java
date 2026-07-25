package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "basic_academical_culture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicAcademicalCultureLectureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id")
	private LectureJpaEntity lectureJpaEntity;

	private String college;
	private String sourcePolicyKey;
	private String mappingKey;
	private String major;
	private Integer startEntryYear;
	private Integer endEntryYear;
	private Integer startTakenYear;
	@Enumerated(EnumType.STRING)
	private Semester startTakenSemester;
	private Integer endTakenYear;
	@Enumerated(EnumType.STRING)
	private Semester endTakenSemester;

	@Builder
	private BasicAcademicalCultureLectureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity,
		String college, String sourcePolicyKey, String mappingKey,
		String major, Integer startEntryYear, Integer endEntryYear,
		Integer startTakenYear, Semester startTakenSemester,
		Integer endTakenYear, Semester endTakenSemester) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.college = college;
		this.sourcePolicyKey = sourcePolicyKey;
		this.mappingKey = mappingKey;
		this.major = major;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
		this.startTakenYear = startTakenYear;
		this.startTakenSemester = startTakenSemester;
		this.endTakenYear = endTakenYear;
		this.endTakenSemester = endTakenSemester;
	}
}
