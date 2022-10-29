package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EntryYearConverter implements AttributeConverter<EntryYear, Integer> {
  @Override
  public Integer convertToDatabaseColumn(EntryYear entryYear) {
    return entryYear.getValue();
  }

  @Override
  public EntryYear convertToEntityAttribute(Integer dbData) {
    return EntryYear.of(dbData);
  }
}
