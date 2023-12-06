package com.plzgraduate.myongjigraduatebe.support;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin.SignInController;
import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.token.TokenController;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenUseCase;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;
import com.plzgraduate.myongjigraduatebe.core.config.QuerydslConfig;
import com.plzgraduate.myongjigraduatebe.core.config.SecurityConfig;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.CalculateGraduationController;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.in.web.SearchLectureController;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web.ParsingTextController;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.find.FindTakenLectureController;
import com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update.UpdateTakenLectureController;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.findauthid.FindAuthIdController;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.finduserinformation.FindUserInformationController;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.resetpassword.ResetPasswordController;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup.SignUpController;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw.WithDrawController;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.CheckAuthIdDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.CheckStudentNumberDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.signup.SignUpUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawUserUseCase;

@ActiveProfiles("test")
@ComponentScan(
	basePackageClasses = {SecurityConfig.class, TokenProvider.class},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {QuerydslConfig.class,
		JpaAuditingConfig.class})
)
@WebMvcTest(controllers = {
	SignInController.class,
	TokenController.class,
	CalculateGraduationController.class,
	SearchLectureController.class,
	FindUserInformationController.class,
	UpdateTakenLectureController.class,
	ParsingTextController.class,
	WithDrawController.class,
	FindTakenLectureController.class,
	ResetPasswordController.class,
	SignUpController.class,
	FindAuthIdController.class
})
public abstract class WebAdaptorTestSupport {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected FindUserUseCase findUserUseCase;

	@MockBean
	protected SignInUseCase signInUseCase;

	@MockBean
	protected TokenUseCase tokenUseCase;

	@MockBean
	protected CalculateGraduationUseCase calculateGraduationUseCase;

	@MockBean
	protected SearchLectureUseCase searchLectureUseCase;

	@MockBean
	protected UpdateTakenLectureUseCase updateTakenLectureUseCase;

	@MockBean
	protected FindTakenLectureUseCase findTakenLectureUseCase;

	@MockBean
	protected FindUserInformationUseCase findUserInformationUseCase;

	@MockBean
	protected FindUserAuthIdUseCase findUserAuthIdUseCase;

	@MockBean
	protected ValidateUserUseCase validateUserUseCase;

	@MockBean
	protected ResetPasswordUseCase resetPasswordUseCase;

	@MockBean
	protected WithDrawUserUseCase withDrawUserUseCase;

	@MockBean
	protected ParsingTextUseCase parsingTextUseCase;

	@MockBean
	protected ParsingTextHistoryUseCase parsingTextHistoryUseCase;

	@MockBean
	protected SignUpUseCase signUpUseCase;

	@MockBean
	protected CheckAuthIdDuplicationUseCase checkAuthIdDuplicationUseCase;

	@MockBean
	protected CheckStudentNumberDuplicationUseCase checkStudentNumberDuplicationUseCase;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

}
