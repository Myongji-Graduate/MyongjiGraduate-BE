package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;

public interface FindCoreCulturePort {

	Set<CoreCulture> findCoreCulture(User user);
}
