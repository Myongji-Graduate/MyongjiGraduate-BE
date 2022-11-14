package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class YearConverter implements AttributeConverter<Year, String> {

  @Override
  public String convertToDatabaseColumn(Year year) {
    return year != null ? year.getValue() : null;
  }

  @Override
  public Year convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Year.of(dbData);
  }
}
