package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserIdConverter implements AttributeConverter<UserId, String> {

  @Override
  public String convertToDatabaseColumn(UserId userId) {
    return userId.getId();
  }

  @Override
  public UserId convertToEntityAttribute(String dbData) {
    return UserId.valueOf(dbData);
  }
}
