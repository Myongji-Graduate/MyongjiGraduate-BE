package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserIdConverter implements AttributeConverter<UserId, String> {

  @Override
  public String convertToDatabaseColumn(UserId userId) {
    return null;
  }

  @Override
  public UserId convertToEntityAttribute(String dbData) {
    return null;
  }
}
