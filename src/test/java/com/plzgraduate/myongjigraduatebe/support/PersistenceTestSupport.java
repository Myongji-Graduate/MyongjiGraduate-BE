package com.plzgraduate.myongjigraduatebe.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public abstract class PersistenceTestSupport {
	public static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8").withDatabaseName("mju-graduate");
}
