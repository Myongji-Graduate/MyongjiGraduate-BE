package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsingTextRepository extends JpaRepository<ParsingTextHistoryJpaEntity, Long> {

}
