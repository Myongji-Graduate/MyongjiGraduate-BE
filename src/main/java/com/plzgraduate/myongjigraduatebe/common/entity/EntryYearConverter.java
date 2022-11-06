package com.plzgraduate.myongjigraduatebe.common.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EntryYearConverter implements AttributeConverter<EntryYear, Integer> {
  @Override
  public Integer convertToDatabaseColumn(EntryYear entryYear) {
    return entryYear != null ? entryYear.getValue() : null;
  }

  @Override
  public EntryYear convertToEntityAttribute(Integer dbData) {
    return dbData != null ? EntryYear.of(dbData) : null;
  }
}
