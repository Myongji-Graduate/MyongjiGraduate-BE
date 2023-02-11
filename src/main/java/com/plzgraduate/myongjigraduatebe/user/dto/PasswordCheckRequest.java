package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordCheckRequest {
    private UserId userId;
    private StudentNumber studentNumber;
}
