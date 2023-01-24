package com.plzgraduate.myongjigraduatebe.user.validator;

import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.dto.PasswordResetRequest;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;
import java.util.Optional;
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
        StudentNumber studentNumber = StudentNumber.valueOf(passwordResetRequest.getStudentNumber());
        UserId userId = UserId.valueOf(passwordResetRequest.getUserId());
        Password newPassword = Password.valueOf(passwordResetRequest.getNewPassword());
        Password passwordCheck = Password.valueOf(passwordResetRequest.getPasswordCheck());
        Optional<User> byUserId = userRepository.findByUserId(userId);

        if(!(passwordCheck.equals(newPassword))){
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        if(!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("해당 아이디의 사용자를 찾을 수 없습니다.");
        }

        if(!userRepository.existsByStudentNumber(studentNumber)){
            throw new IllegalArgumentException("해당 학번의 사용자를 찾을 수 없습니다.");
        }
        if(byUserId.isPresent() && !byUserId.get().getStudentNumber().equals(studentNumber)){
            throw new IllegalArgumentException("해당 아이디와 일치하는 학번이 없습니다.");
        }
    }
}
