package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.plzgraduate.myongjigraduatebe.core.entity.TimeBaseEntity;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ParsingTextHistoryJpaEntity extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	private UserJpaEntity user;

	private String parsingText;

	@Enumerated(EnumType.STRING)
	private ParsingResult parsingResult;
}
