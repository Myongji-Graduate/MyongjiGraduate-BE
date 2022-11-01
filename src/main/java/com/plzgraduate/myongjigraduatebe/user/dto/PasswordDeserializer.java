package com.plzgraduate.myongjigraduatebe.user.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PasswordDeserializer extends JsonDeserializer<Password> {
  @Override
  public Password deserialize(
      JsonParser p,
      DeserializationContext ctxt
  ) throws IOException {
    return Password.of(p.getText());
  }
}
