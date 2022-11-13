package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInitCheckResponse {

  private final boolean isValidToken;

  private final boolean isInit;

}
