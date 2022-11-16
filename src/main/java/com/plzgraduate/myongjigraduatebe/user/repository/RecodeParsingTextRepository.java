package com.plzgraduate.myongjigraduatebe.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plzgraduate.myongjigraduatebe.user.entity.RecodeParsingText;

@Repository
public interface RecodeParsingTextRepository extends JpaRepository<RecodeParsingText, Long> {
}
