package com.plzgraduate.myongjigraduatebe.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.application.port.SignUpUseCase;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserPersistenceAdaptor;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService implements SignUpUseCase {

	private final UserPersistenceAdaptor userPersistenceAdaptor;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void signUp(SignUpRequest signUpRequest) {
		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
		EnglishLevel englishLevel = EnglishLevel.valueOf(signUpRequest.getEngLv());
		User newUser = User.create(signUpRequest.getAuthId(), encodedPassword, englishLevel,
			signUpRequest.getStudentNumber());
		userPersistenceAdaptor.saveUser(newUser);
	}
}
