package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out.persistence;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.plzgraduate.myongjigraduatebe.core.entity.TimeBaseEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "taken_lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class TakenLectureJpaEntity extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserJpaEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	private LectureJpaEntity lecture;

	private Integer year;

	private Semester semester;

}
