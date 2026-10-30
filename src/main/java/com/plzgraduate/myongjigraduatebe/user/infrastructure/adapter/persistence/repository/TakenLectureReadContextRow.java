package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;

public record TakenLectureReadContextRow(UserJpaEntity user, long revision, Long catalogRevision) { }
