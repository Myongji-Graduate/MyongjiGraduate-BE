package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private BasicAcademicalCultureLectureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity, String college) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.college = college;
	}
}
