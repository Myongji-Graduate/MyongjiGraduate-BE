package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecodeParsingText extends BaseEntity {

  public RecodeParsingText(
      Long userId,
      String parsingText
  ) {
    this.userId = userId;
    this.parsingText = parsingText;
  }

  @Column(columnDefinition = "BIGINT")
  Long userId;

  @Column(columnDefinition = "TEXT")
  String parsingText;

  @Setter
  @Enumerated(EnumType.STRING)
  ParsingResult parsingResult;
}
