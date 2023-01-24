package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    private String userId;
    private String studentNumber;
    private String newPassword;
    private String passwordCheck;

}
