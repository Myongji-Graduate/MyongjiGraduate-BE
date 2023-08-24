package com.plzgraduate.myongjigraduatebe.auth.application.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.auth.CustomUserDetails;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final LoadUserPort loadUserPort;

	@Override
	public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return new CustomUserDetails(loadUserPort.loadUserByUserId(userId));
	}
}
