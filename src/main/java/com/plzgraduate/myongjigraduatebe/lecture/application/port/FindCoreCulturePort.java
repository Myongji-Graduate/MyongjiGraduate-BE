package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindCoreCulturePort {

	Set<CoreCulture> findCoreCulture(User user);
}
