package com.plzgraduate.myongjigraduatebe.support;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;
import com.plzgraduate.myongjigraduatebe.core.config.QuerydslConfig;
import com.plzgraduate.myongjigraduatebe.core.config.SecurityConfig;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;

@ActiveProfiles("test")
@ComponentScan(
	basePackageClasses = {SecurityConfig.class, TokenProvider.class},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {QuerydslConfig.class, JpaAuditingConfig.class})
)
public abstract class WebAdaptorTestSupport {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected FindUserUseCase findUserUseCase;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

}
