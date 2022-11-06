package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StudentNumberConverter implements AttributeConverter<StudentNumber, String> {
  @Override
  public String convertToDatabaseColumn(StudentNumber studentNumber) {
    return studentNumber.getValue();
  }

  @Override
  public StudentNumber convertToEntityAttribute(String dbData) {
    return StudentNumber.valueOf(dbData);
  }
}
