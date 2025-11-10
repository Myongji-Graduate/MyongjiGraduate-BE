package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
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
@Table(name = "common_culture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCultureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id")
	private LectureJpaEntity lectureJpaEntity;

	@Enumerated(value = EnumType.STRING)
	private CommonCultureCategory commonCultureCategory;

	private int startEntryYear;

	private int endEntryYear;

	@Builder
	private CommonCultureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity,
		CommonCultureCategory commonCultureCategory, int startEntryYear, int endEntryYear) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.commonCultureCategory = commonCultureCategory;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
	}
}
