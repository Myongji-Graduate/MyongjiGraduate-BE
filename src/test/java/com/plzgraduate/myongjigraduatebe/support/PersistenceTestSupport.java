package com.plzgraduate.myongjigraduatebe.support;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
public abstract class PersistenceTestSupport {
	@Container
	protected static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8").withDatabaseName("mju-graduate");

	@PersistenceContext
	protected EntityManager entityManager;

}
