package com.plzgraduate.myongjigraduatebe.core.meta;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.plzgraduate.myongjigraduatebe.auth.CustomUserDetails;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(LoginUser.class)
			&& Long.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			log.warn("authentication in security context is null");
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof Long) {
			return principal;
		}
		return null;
	}
}
