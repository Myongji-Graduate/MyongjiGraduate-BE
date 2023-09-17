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
@Table(name = "major")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorLectureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id")
	private LectureJpaEntity lectureJpaEntity;

	private String major;

	private int mandatory;

	private int startEntryYear;

	private int endEntryYear;

	@Builder
	private MajorLectureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity, String major, int mandatory,
		int startEntryYear,
		int endEntryYear) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.major = major;
		this.mandatory = mandatory;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
	}
}
