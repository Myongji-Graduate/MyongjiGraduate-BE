package com.plzgraduate.myongjigraduatebe.user.validator;

import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.dto.PasswordResetRequest;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordResetRequestValidator implements Validator {
    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordResetRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordResetRequest passwordResetRequest = (PasswordResetRequest) target;
        UserId userId = UserId.valueOf(passwordResetRequest.getUserId());
        Password newPassword = Password.valueOf(passwordResetRequest.getNewPassword());
        Password passwordCheck = Password.valueOf(passwordResetRequest.getPasswordCheck());

        if(!(passwordCheck.equals(newPassword))){
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        if(!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다.");
        }
    }
}
