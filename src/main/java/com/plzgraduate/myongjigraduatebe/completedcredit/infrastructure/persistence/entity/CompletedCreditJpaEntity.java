package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
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
