package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureFromParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class ParsingTextServiceTest{
	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private UpdateStudentInformationUseCase updateStudentInformationUseCase;
	@Mock
	private SaveTakenLectureFromParsingTextUseCase saveTakenLectureFromParsingTextUseCase;
	@Mock
	private DeleteTakenLectureByUserUseCase deleteTakenLectureByUserUseCase;
	@InjectMocks
	private ParsingTextService parsingTextService;

	@DisplayName("파싱 텍스트에서 정보를 추출해 성공적으로 메서드를 호출한다.")
	@Test
	void 성공() {
	    //given
		User user = createUser(1L, "mju1001!", "1q2w3e4r!", EnglishLevel.ENG12, "정지환",
			"60181666", 18, "융합소프트웨어학부", null, StudentCategory.NORMAL);
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 정지환(60181666), 현학적 - 재학, 이수 - 7, 입학 - 신입학(2018/03/02)"
			+ "|토익 - 570, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2022/07/15)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 17, 핵심교양 12, 학문기초교양 18, 일반교양 5, 전공 63, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 3"
			+ "|총 취득학점 - 118, 총점 - 436, 평균평점 - 3.82"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양|2022년 동계계절|교필104|KMA02104|글쓰기|3|C0|공통교양|2020년 1학기|교필122|KMA02122|기독교와문화|2|B0|공통교양|2019년 2학기|교필127|KMA00101|성서와인간이해|2|B+|공통교양|2019년 하계계절|교필106|KMA02106|영어1|2|B+|2|공통교양|2019년 2학기|교필107|KMA02107|영어2|2|B+|공통교양|2022년 동계계절|교필108|KMA02108|영어회화1|1|B+|3|공통교양|2023년 1학기|교필109|KMA02109|영어회화2|1|A0|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2022년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2022년 2학기|교선130|KMA02130|고전으로읽는인문학|3|A0|핵심교양|2019년 2학기|교선113|KMA02113|세계화와사회변화|3|B+|핵심교양|2018년 1학기|교선135|KMA02135|우주,생명,마음|3|C+|핵심교양|2020년 2학기|교선111|KMA02111|한국근현대사의이해|3|A+|학문기초교양|2019년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|B+|학문기초교양|2022년 2학기|기사123|KMD02123|경제학원론|3|A+|학문기초교양|2022년 2학기|기사103|KMD02103|대중문화와매스컴|3|A0|학문기초교양|2019년 1학기|기자107|KME02107|선형대수학개론|3|B0|6|학문기초교양|2019년 하계계절|기인107|KMB02107|인간심리의이해|3|B+|5|학문기초교양|2020년 2학기|기자106|KME02106|통계학개론|3|A+|일반교양|2022년 2학기|균여111|KMO02111|교양볼링|2|B+|일반교양|2020년 1학기|기컴118|KMI02118|엑셀기초및실무활용|3|B+|4|전공1단계|2019년 1학기|데테202|HED01202|R통계분석|3|A0|전공1단계|2022년 2학기|데테314|HED01314|게임프로그래밍|3|A+|전공1단계|2020년 1학기|데테316|HED01316|고급웹프로그래밍|3|A0|전공1단계|2019년 2학기|데테206|HED01206|기초웹프로그래밍|3|B+|전공1단계|2019년 1학기|융소102|HEB01102|기초프로그래밍|3|B+|1|전공1단계|2019년 2학기|융소105|HEB01105|기초프로그래밍2|3|C+|전공1단계|2019년 2학기|데테318|HED01203|데이터베이스|3|A0|전공1단계|2023년 1학기|데테317|HED01317|데이터베이스프로젝트|3|A+|전공1단계|2023년 1학기|데테407|HED01407|딥러닝|3|B+|전공1단계|2023년 1학기|데테319|HED01318|모바일컴퓨팅|3|A+|전공1단계|2020년 2학기|데테403|HED01403|블록체인기초|3|A+|전공1단계|2023년 1학기|데테404|HED01404|빅데이터기술특론1|3|B0|전공1단계|2022년 2학기|데테306|HED01306|빅데이터프로그래밍|3|A0|전공1단계|2020년 1학기|데테301|HED01301|소프트웨어공학|3|A+|전공1단계|2020년 2학기|데테209|HED01307|알고리즘|3|A+|전공1단계|2020년 2학기|데테303|HED01303|운영체제|3|A+|전공1단계|2020년 1학기|데테315|HED01315|인공지능|3|B+|전공1단계|2019년 1학기|데테201|HED01201|자료구조|3|A0|전공1단계|2023년 1학기|데테413|HED01413|캡스톤디자인|3|A+|전공1단계|2020년 1학기|데테309|HED01309|컴퓨터아키텍쳐|3|A0|전공1단계|2020년 2학기|데테313|HED01313|컴퓨터통신|3|A+|자유선택|2023년 1학기|교선136|KMA02136|SW프로그래밍입문|3|A+|공통교양|2019년 2학기|교필101|KMA02101|채플|0.5|N|공통교양|2018년 1학기|교필106|KMA02106|영어1|0|R|2|공통교양|2018년 1학기|교필108|KMA02108|영어회화1|0|R|3|공통교양|2020년 1학기|교필108|KMA02108|영어회화1|0|R|3|학문기초교양|2018년 1학기|기자107|KME02107|선형대수학개론|0|R|6|학문기초교양|2018년 1학기|기인107|KMB02107|인간심리의이해|0|R|5|일반교양|2018년 1학기|기컴118|KMI02118|엑셀기초및실무활용|0|R|4|전공1단계|2018년 1학기|융소102|HEB01102|기초프로그래밍|0|R|1|";

		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(1L)
			.parsingText(parsingText)
			.build();

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		//when
		parsingTextService.enrollParsingText(command);

	    //then
		then(updateStudentInformationUseCase).should().updateUser(any(UpdateStudentInformationCommand.class));
		then(deleteTakenLectureByUserUseCase).should().deleteAllTakenLecturesByUser(any(User.class));
		then(saveTakenLectureFromParsingTextUseCase).should().saveTakenLectures(any(SaveTakenLectureCommand.class));
	}

	@DisplayName("PDF 파싱 텍스트가 빈 문자열로 오면 InvalidPdfException을 반환한다.")
	@Test
	void 파싱_텍스트_빈문자열() {
	    //given
		String emptyParsingText = " ";
		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(1L)
			.parsingText(emptyParsingText)
			.build();

	    //when //then
	    assertThatThrownBy(() ->  parsingTextService.enrollParsingText(command))
			.isInstanceOf(InvalidPdfException.class)
			.hasMessage("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
	}

	@DisplayName("사용자의 학번과 PDF의 학번이 다를때 InvalidPdfException을 반환한다.")
	@Test
	void 서로다른_PDF_학번() {
	    //given
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 정지환(60181666), 현학적 - 재학, 이수 - 7, 입학 - 신입학(2018/03/02)"
			+ "|토익 - 570, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2022/07/15)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 17, 핵심교양 12, 학문기초교양 18, 일반교양 5, 전공 63, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 3"
			+ "|총 취득학점 - 118, 총점 - 436, 평균평점 - 3.82"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양|2022년 동계계절|교필104|KMA02104|글쓰기|3|C0|공통교양|2020년 1학기|교필122|KMA02122|기독교와문화|2|B0|공통교양|2019년 2학기|교필127|KMA00101|성서와인간이해|2|B+|공통교양|2019년 하계계절|교필106|KMA02106|영어1|2|B+|2|공통교양|2019년 2학기|교필107|KMA02107|영어2|2|B+|공통교양|2022년 동계계절|교필108|KMA02108|영어회화1|1|B+|3|공통교양|2023년 1학기|교필109|KMA02109|영어회화2|1|A0|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2022년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2022년 2학기|교선130|KMA02130|고전으로읽는인문학|3|A0|핵심교양|2019년 2학기|교선113|KMA02113|세계화와사회변화|3|B+|핵심교양|2018년 1학기|교선135|KMA02135|우주,생명,마음|3|C+|핵심교양|2020년 2학기|교선111|KMA02111|한국근현대사의이해|3|A+|학문기초교양|2019년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|B+|학문기초교양|2022년 2학기|기사123|KMD02123|경제학원론|3|A+|학문기초교양|2022년 2학기|기사103|KMD02103|대중문화와매스컴|3|A0|학문기초교양|2019년 1학기|기자107|KME02107|선형대수학개론|3|B0|6|학문기초교양|2019년 하계계절|기인107|KMB02107|인간심리의이해|3|B+|5|학문기초교양|2020년 2학기|기자106|KME02106|통계학개론|3|A+|일반교양|2022년 2학기|균여111|KMO02111|교양볼링|2|B+|일반교양|2020년 1학기|기컴118|KMI02118|엑셀기초및실무활용|3|B+|4|전공1단계|2019년 1학기|데테202|HED01202|R통계분석|3|A0|전공1단계|2022년 2학기|데테314|HED01314|게임프로그래밍|3|A+|전공1단계|2020년 1학기|데테316|HED01316|고급웹프로그래밍|3|A0|전공1단계|2019년 2학기|데테206|HED01206|기초웹프로그래밍|3|B+|전공1단계|2019년 1학기|융소102|HEB01102|기초프로그래밍|3|B+|1|전공1단계|2019년 2학기|융소105|HEB01105|기초프로그래밍2|3|C+|전공1단계|2019년 2학기|데테318|HED01203|데이터베이스|3|A0|전공1단계|2023년 1학기|데테317|HED01317|데이터베이스프로젝트|3|A+|전공1단계|2023년 1학기|데테407|HED01407|딥러닝|3|B+|전공1단계|2023년 1학기|데테319|HED01318|모바일컴퓨팅|3|A+|전공1단계|2020년 2학기|데테403|HED01403|블록체인기초|3|A+|전공1단계|2023년 1학기|데테404|HED01404|빅데이터기술특론1|3|B0|전공1단계|2022년 2학기|데테306|HED01306|빅데이터프로그래밍|3|A0|전공1단계|2020년 1학기|데테301|HED01301|소프트웨어공학|3|A+|전공1단계|2020년 2학기|데테209|HED01307|알고리즘|3|A+|전공1단계|2020년 2학기|데테303|HED01303|운영체제|3|A+|전공1단계|2020년 1학기|데테315|HED01315|인공지능|3|B+|전공1단계|2019년 1학기|데테201|HED01201|자료구조|3|A0|전공1단계|2023년 1학기|데테413|HED01413|캡스톤디자인|3|A+|전공1단계|2020년 1학기|데테309|HED01309|컴퓨터아키텍쳐|3|A0|전공1단계|2020년 2학기|데테313|HED01313|컴퓨터통신|3|A+|자유선택|2023년 1학기|교선136|KMA02136|SW프로그래밍입문|3|A+|공통교양|2019년 2학기|교필101|KMA02101|채플|0.5|N|공통교양|2018년 1학기|교필106|KMA02106|영어1|0|R|2|공통교양|2018년 1학기|교필108|KMA02108|영어회화1|0|R|3|공통교양|2020년 1학기|교필108|KMA02108|영어회화1|0|R|3|학문기초교양|2018년 1학기|기자107|KME02107|선형대수학개론|0|R|6|학문기초교양|2018년 1학기|기인107|KMB02107|인간심리의이해|0|R|5|일반교양|2018년 1학기|기컴118|KMI02118|엑셀기초및실무활용|0|R|4|전공1단계|2018년 1학기|융소102|HEB01102|기초프로그래밍|0|R|1|";

		User user = createUser(1L, "mju1001!", "1q2w3e4r!", EnglishLevel.ENG12, "정지환",
			"60181665", 18, "융합소프트웨어학부", null, StudentCategory.NORMAL);

		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(1L)
			.parsingText(parsingText)
			.build();

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);

	    //when //then

		assertThatThrownBy(() -> parsingTextService.enrollParsingText(command))
			.isInstanceOf(InvalidPdfException.class)
			.hasMessage("본인의 학번과 PDF 학번이 일치하지 않습니다.");
	}

	@DisplayName("RuntimeException이 발생했을 경우 PdfParsingException을 반환한다.")
	@Test
	void PDF_파싱_예외처리() {
	    //given
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 정지환(60181666), 현학적 - 재학, 이수 - 7, 입학 - 신입학(2018/03/02)"
			+ "|토익 - 570, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2022/07/15)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 17, 핵심교양 12, 학문기초교양 18, 일반교양 5, 전공 63, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 3"
			+ "|총 취득학점 - 118, 총점 - 436, 평균평점 - 3.82"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양|2022년 동계계절|교필104|KMA02104|글쓰기|3|C0|공통교양|2020년 1학기|교필122|KMA02122|기독교와문화|2|B0|공통교양|2019년 2학기|교필127|KMA00101|성서와인간이해|2|B+|공통교양|2019년 하계계절|교필106|KMA02106|영어1|2|B+|2|공통교양|2019년 2학기|교필107|KMA02107|영어2|2|B+|공통교양|2022년 동계계절|교필108|KMA02108|영어회화1|1|B+|3|공통교양|2023년 1학기|교필109|KMA02109|영어회화2|1|A0|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2022년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2022년 2학기|교선130|KMA02130|고전으로읽는인문학|3|A0|핵심교양|2019년 2학기|교선113|KMA02113|세계화와사회변화|3|B+|핵심교양|2018년 1학기|교선135|KMA02135|우주,생명,마음|3|C+|핵심교양|2020년 2학기|교선111|KMA02111|한국근현대사의이해|3|A+|학문기초교양|2019년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|B+|학문기초교양|2022년 2학기|기사123|KMD02123|경제학원론|3|A+|학문기초교양|2022년 2학기|기사103|KMD02103|대중문화와매스컴|3|A0|학문기초교양|2019년 1학기|기자107|KME02107|선형대수학개론|3|B0|6|학문기초교양|2019년 하계계절|기인107|KMB02107|인간심리의이해|3|B+|5|학문기초교양|2020년 2학기|기자106|KME02106|통계학개론|3|A+|일반교양|2022년 2학기|균여111|KMO02111|교양볼링|2|B+|일반교양|2020년 1학기|기컴118|KMI02118|엑셀기초및실무활용|3|B+|4|전공1단계|2019년 1학기|데테202|HED01202|R통계분석|3|A0|전공1단계|2022년 2학기|데테314|HED01314|게임프로그래밍|3|A+|전공1단계|2020년 1학기|데테316|HED01316|고급웹프로그래밍|3|A0|전공1단계|2019년 2학기|데테206|HED01206|기초웹프로그래밍|3|B+|전공1단계|2019년 1학기|융소102|HEB01102|기초프로그래밍|3|B+|1|전공1단계|2019년 2학기|융소105|HEB01105|기초프로그래밍2|3|C+|전공1단계|2019년 2학기|데테318|HED01203|데이터베이스|3|A0|전공1단계|2023년 1학기|데테317|HED01317|데이터베이스프로젝트|3|A+|전공1단계|2023년 1학기|데테407|HED01407|딥러닝|3|B+|전공1단계|2023년 1학기|데테319|HED01318|모바일컴퓨팅|3|A+|전공1단계|2020년 2학기|데테403|HED01403|블록체인기초|3|A+|전공1단계|2023년 1학기|데테404|HED01404|빅데이터기술특론1|3|B0|전공1단계|2022년 2학기|데테306|HED01306|빅데이터프로그래밍|3|A0|전공1단계|2020년 1학기|데테301|HED01301|소프트웨어공학|3|A+|전공1단계|2020년 2학기|데테209|HED01307|알고리즘|3|A+|전공1단계|2020년 2학기|데테303|HED01303|운영체제|3|A+|전공1단계|2020년 1학기|데테315|HED01315|인공지능|3|B+|전공1단계|2019년 1학기|데테201|HED01201|자료구조|3|A0|전공1단계|2023년 1학기|데테413|HED01413|캡스톤디자인|3|A+|전공1단계|2020년 1학기|데테309|HED01309|컴퓨터아키텍쳐|3|A0|전공1단계|2020년 2학기|데테313|HED01313|컴퓨터통신|3|A+|자유선택|2023년 1학기|교선136|KMA02136|SW프로그래밍입문|3|A+|공통교양|2019년 2학기|교필101|KMA02101|채플|0.5|N|공통교양|2018년 1학기|교필106|KMA02106|영어1|0|R|2|공통교양|2018년 1학기|교필108|KMA02108|영어회화1|0|R|3|공통교양|2020년 1학기|교필108|KMA02108|영어회화1|0|R|3|학문기초교양|2018년 1학기|기자107|KME02107|선형대수학개론|0|R|6|학문기초교양|2018년 1학기|기인107|KMB02107|인간심리의이해|0|R|5|일반교양|2018년 1학기|기컴118|KMI02118|엑셀기초및실무활용|0|R|4|전공1단계|2018년 1학기|융소102|HEB01102|기초프로그래밍|0|R|1|";

		User user = createUser(1L, "mju1001!", "1q2w3e4r!", EnglishLevel.ENG12, "정지환",
			"60181666", 18, "융합소프트웨어학부", null, StudentCategory.NORMAL);

	    //when
		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(1L)
			.parsingText(parsingText)
			.build();

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		doThrow(NullPointerException.class).when(saveTakenLectureFromParsingTextUseCase)
			.saveTakenLectures(any(SaveTakenLectureCommand.class));

		//then
		assertThatThrownBy(() -> parsingTextService.enrollParsingText(command))
			.isInstanceOf(PdfParsingException.class)
			.hasMessage("PDF에서 정보를 읽어오는데 실패했습니다. 채널톡으로 문의 바랍니다.");
	}

	private User createUser(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return User.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
