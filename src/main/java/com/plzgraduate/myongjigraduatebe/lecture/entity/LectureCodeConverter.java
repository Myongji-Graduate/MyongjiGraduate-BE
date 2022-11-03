package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter
public class LectureCodeConverter implements AttributeConverter<LectureCode, String> {
  @Override
  public String convertToDatabaseColumn(LectureCode lectureCode) {
    return lectureCode.getCode();
  }

  @Override
  public LectureCode convertToEntityAttribute(String dbData) {
    return dbData==null ? null : LectureCode.of(dbData) ;
  }
}
