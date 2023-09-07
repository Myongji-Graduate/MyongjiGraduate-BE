package com.plzgraduate.myongjigraduatebe.auth.jwt;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.FindUserUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CustomUserDetailService implements UserDetailsService {

	private final FindUserUseCase findUserUseCase;

	@Override
	public CustomUserDetails loadUserByUsername(String authId) throws UsernameNotFoundException {
		return new CustomUserDetails(findUserUseCase.findUserByAuthId(authId));
	}
}
