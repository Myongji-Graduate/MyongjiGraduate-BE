package com.plzgraduate.myongjigraduatebe.support;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MysqlTestContainer {

	@Container
	protected static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8")
		.withDatabaseName("mju-graduate");

}
