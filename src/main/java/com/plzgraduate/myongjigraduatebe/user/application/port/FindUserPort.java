package com.plzgraduate.myongjigraduatebe.user.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Optional;

public interface FindUserPort {

	Optional<User> findUserById(Long id);

    /** Holds a write lock on the user until the caller's transaction completes. */
    Optional<User> findUserByIdForUpdate(Long id);

	Optional<User> findUserByAuthId(String authId);

	Optional<User> findUserByStudentNumber(String studentNumber);
}
