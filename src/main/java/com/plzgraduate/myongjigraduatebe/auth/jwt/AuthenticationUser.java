package com.plzgraduate.myongjigraduatebe.auth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticationUser {
	private final Long id;
	private final String authId;

}
