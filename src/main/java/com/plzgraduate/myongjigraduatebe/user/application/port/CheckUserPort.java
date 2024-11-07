package com.plzgraduate.myongjigraduatebe.user.application.port;

public interface CheckUserPort {

	boolean checkDuplicateAuthId(String authId);

	boolean checkDuplicateStudentNumber(String studentNumber);
}
