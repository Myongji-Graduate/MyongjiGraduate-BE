package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String lectureCode;

	private String name;

	private int credit;

	private String duplicateCode;

	private int isRevoked;

	@Builder
	private LectureJpaEntity(Long id, String lectureCode, String name, int credit, String duplicateCode, int isRevoked) {
		this.id = id;
		this.lectureCode = lectureCode;
		this.name = name;
		this.credit = credit;
		this.duplicateCode = duplicateCode;
		this.isRevoked = isRevoked;
	}
}
