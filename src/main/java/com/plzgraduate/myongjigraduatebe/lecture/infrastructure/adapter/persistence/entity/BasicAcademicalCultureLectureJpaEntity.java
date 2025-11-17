package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
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

	@Builder
	private BasicAcademicalCultureLectureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity,
		String college) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.college = college;
	}
}
