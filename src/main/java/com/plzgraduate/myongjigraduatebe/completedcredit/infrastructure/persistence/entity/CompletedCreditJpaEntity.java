package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "completed_credit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedCreditJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserJpaEntity userJpaEntity;

	@Enumerated(value = EnumType.STRING)
	private GraduationCategory graduationCategory;

	private double totalCredit;

	private double takenCredit;

	@Builder
	private CompletedCreditJpaEntity(Long id, UserJpaEntity userJpaEntity,
		GraduationCategory graduationCategory,
		double totalCredit, double takenCredit) {
		this.id = id;
		this.userJpaEntity = userJpaEntity;
		this.graduationCategory = graduationCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
