package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.plzgraduate.myongjigraduatebe.core.entity.TimeBaseEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "core_culture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoreCultureJpaEntity extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id")
	private LectureJpaEntity lectureJpaEntity;

	@Enumerated(value = EnumType.STRING)
	private CoreCultureCategory coreCultureCategory;

	private int startEntryYear;

	private int endEntryYear;

	@Builder
	private CoreCultureJpaEntity(Long id, LectureJpaEntity lectureJpaEntity, CoreCultureCategory coreCultureCategory,
		int startEntryYear, int endEntryYear) {
		this.id = id;
		this.lectureJpaEntity = lectureJpaEntity;
		this.coreCultureCategory = coreCultureCategory;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
	}
}
