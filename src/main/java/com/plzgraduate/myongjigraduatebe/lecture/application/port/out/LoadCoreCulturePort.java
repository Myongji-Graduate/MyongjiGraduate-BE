package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadCoreCulturePort {

	Set<CoreCulture> loadCoreCulture(User user);
}
