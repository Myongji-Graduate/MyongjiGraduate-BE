package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity;

import com.plzgraduate.myongjigraduatebe.core.entity.TimeBaseEntity;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "parsing_text_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ParsingTextHistoryJpaEntity extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserJpaEntity user;

	@Column(length = 5000)
	private String parsingText;

	@Enumerated(EnumType.STRING)
	private ParsingResult parsingResult;

	@Enumerated(EnumType.STRING)
	@Column(length = 100)
	private FailureReason failureReason;

	@Column(columnDefinition = "TEXT")
	private String failureDetails;
}
