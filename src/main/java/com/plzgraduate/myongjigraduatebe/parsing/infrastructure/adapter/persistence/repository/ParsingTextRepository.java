package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsingTextRepository extends JpaRepository<ParsingTextHistoryJpaEntity, Long> {

	void deleteAllByUser(UserJpaEntity user);

	List<ParsingTextHistoryJpaEntity> findByParsingResultAndFailureReasonIsNull(ParsingResult parsingResult);

}
