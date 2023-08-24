package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String authId;

	private String password;

	@Enumerated(value = EnumType.STRING)
	private EnglishLevel englishLevel;

	private String name;

	private String studentNumber;

	private int entryYear;

	private String major;

	private String subMajor;

	@Enumerated(value = EnumType.STRING)
	private StudentCategory studentCategory;

	@Builder
	private  UserJpaEntity(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		this.id = id;
		this.authId = authId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.entryYear = entryYear;
		this.major = major;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
	}
}
