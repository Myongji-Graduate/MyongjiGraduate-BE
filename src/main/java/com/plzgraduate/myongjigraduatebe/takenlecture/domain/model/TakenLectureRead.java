package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

/** Request-owned user and course inventory read from the same transaction snapshot. */
public record TakenLectureRead(User user, TakenLectureInventory inventory) { }
