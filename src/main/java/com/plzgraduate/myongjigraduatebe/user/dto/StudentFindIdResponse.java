package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentFindIdResponse {

    private final String userId;

    private final String studentNumber;

    public static StudentFindIdResponse of(
            String userId,
            String studentNumber
    ) {
        return new StudentFindIdResponse(encodeUserId(userId), studentNumber);
    }

    private static String encodeUserId(String userId){
        return userId.substring(0, userId.length()-3) + "***";
    }
}
