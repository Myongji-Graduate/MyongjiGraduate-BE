package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

public interface ParsingTextRepository extends JpaRepository<ParsingTextHistoryJpaEntity, Long> {

	void deleteAllByUser(UserJpaEntity user);

}
