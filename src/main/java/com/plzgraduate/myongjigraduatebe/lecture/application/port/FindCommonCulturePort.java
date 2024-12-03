package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;

public interface FindCommonCulturePort {

	Set<CommonCulture> findCommonCulture(User user);
}
