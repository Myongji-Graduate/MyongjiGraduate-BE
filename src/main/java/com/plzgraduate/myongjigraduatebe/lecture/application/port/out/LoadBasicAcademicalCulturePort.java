package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadBasicAcademicalCulturePort {

	Set<BasicAcademicalCulture> loadBasicAcademicalCulture(User user);
}
