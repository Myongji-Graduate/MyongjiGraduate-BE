package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadCommonCulturePort {

	Set<CommonCulture> loadCommonCulture(User user);
}
