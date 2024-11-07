package com.plzgraduate.myongjigraduatebe.support;

import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
public abstract class PersistenceTestSupport {

	@PersistenceContext
	protected EntityManager entityManager;

}
