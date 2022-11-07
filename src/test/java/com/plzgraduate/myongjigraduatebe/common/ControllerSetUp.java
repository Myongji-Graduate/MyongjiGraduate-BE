package com.plzgraduate.myongjigraduatebe.common;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.plzgraduate.myongjigraduatebe.auth.service.JwtAuthService;
import com.plzgraduate.myongjigraduatebe.common.config.JwtConfig;
import com.plzgraduate.myongjigraduatebe.common.config.SecurityConfig;

@Import({SecurityConfig.class})
@MockBeans({
    @MockBean(JpaMetamodelMappingContext.class),
    @MockBean(JdbcOperations.class),
    @MockBean(JwtAuthService.class),
    @MockBean(JwtConfig.class),
})
@ExtendWith(RestDocumentationExtension.class)
public class ControllerSetUp {

  protected MockMvc mockMvc;

  @BeforeEach
  public void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentationContextProvider
  ) {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(restDocumentationContextProvider))
        .apply(springSecurity())
        .build();
  }
}
