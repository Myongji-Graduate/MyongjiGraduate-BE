package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.core.entity.TimeBaseEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "user")
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String authId;

	@Column(nullable = false)
	private String password;

	@Enumerated(value = EnumType.STRING)
	private EnglishLevel englishLevel;

	private String name;

	@Column(unique = true, nullable = false)
	private String studentNumber;

	private int entryYear;

	private String major;

	private String dualMajor;

	private String subMajor;

	private String associatedMajor;

	private String transferStatus;

	private int totalCredit;

	private double takenCredit;

	private boolean graduated;

	@Enumerated(value = EnumType.STRING)
	private StudentCategory studentCategory;

}
