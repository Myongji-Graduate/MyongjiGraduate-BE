package com.plzgraduate.myongjigraduatebe.parsing.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;

class ParsingInformationTest {

	@DisplayName("ParsingInformation 생성 시 파싱 텍스트에서 필요한 정보들을 추출한다.")
	@Test
	void createParsingInformation() {
	    //given
		String parsingText = "출력일자 :  2022/11/14|1/1"
			+ "|경영대학 경영학과, 이아현(60191000), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2019/03/04)"
			+ "|토익 - 625, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2021/07/20)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 15, 핵심교양 12, 학문기초교양 6, 일반교양 22, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0"
			+ "|총 취득학점 - 105, 총점 - 368.5, 평균평점 - 4.14"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양 (구 필수교양)|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양 (구 필수교양)|2019년 1학기|교필104|KMA02104|글쓰기|3|A0|공통교양 (구 필수교양)|2019년 1학기|교필127|KMA00101|성서와인간이해|2|A0|공통교양 (구 필수교양)|2019년 2학기|교필106|KMA02106|영어1|2|A0|공통교양 (구 필수교양)|2021년 2학기|교필107|KMA02107|영어2|2|B0|공통교양 (구 필수교양)|2019년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2021년 2학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2019년 2학기|교필102|KMA02102|현대사회와기독교윤리|2|A+|핵심교양 (구 선택교양)|2019년 2학기|교선136|KMA02136|SW프로그래밍입문|3|B+|핵심교양 (구 선택교양)|2019년 2학기|교선128|KMA02128|글로벌문화|3|A+|핵심교양 (구 선택교양)|2019년 1학기|교선114|KMA02114|민주주의와현대사회|3|A0|핵심교양 (구 선택교양)|2020년 1학기|교선112|KMA02112|역사와문명|3|A+|학문기초교양 (구 기초교양)|2020년 2학기|기사107|KMD02107|경상통계학|3|A0|학문기초교양 (구 기초교양)|2020년 1학기|기사114|KMD02114|미시경제학원론|3|A+|일반교양 (구 균형교양)|2020년 2학기|균명103|KMR02103|금융교육특강|2|P|일반교양 (구 균형교양)|2020년 2학기|균인104|KMK02104|데이팅과결혼|3|A+|일반교양 (구 균형교양)|2022년 1학기|기문104|KMC02104|문학과사회|3|A0|일반교양 (구 균형교양)|2019년 2학기|균명101|KMR02101|방목석좌교수특강|2|P|일반교양 (구 균형교양)|2020년 1학기|기컴125|KMI02125|생활속의스마트IT(KCU)|3|A+|일반교양 (구 균형교양)|2020년 2학기|균사148|KMM02148|자기분석과직무역량개발|2|P|일반교양 (구 균형교양)|2020년 2학기|균명901|KMR02901|전공과진로1|1|P|일반교양 (구 균형교양)|2019년 1학기|기공109|KMF02109|전공자유학부세미나|1|P|일반교양 (구 균형교양)|2019년 1학기|균사133|KMM02133|진로선택과대학생활|2|P|일반교양 (구 균형교양)|2019년 1학기|균과117|KMN02117|환경과웰빙(KCU)|3|A+|전공1단계|2021년 2학기|경영373|HCA02373|경영취업세미나|2|P|전공1단계|2021년 2학기|경영366|HCA02366|고급회계|3|A0|전공1단계|2021년 2학기|경영453|HCA02453|관리회계|3|B+|전공1단계|2022년 1학기|경영424|HCA02424|금융기관경영론|3|A+|전공1단계|2022년 1학기|경영348|HCA02348|기업법1|3|A0|전공1단계|2021년 2학기|경영349|HCA02349|기업법2|3|A0|전공1단계|2021년 2학기|경과138|HBX01138|기업재무론|3|A0|전공1단계|2020년 2학기|경과106|HBX01106|마케팅원론|3|A+|전공1단계|2020년 1학기|경과143|HBX01143|운영관리|3|A+|전공1단계|2022년 1학기|경영251|HCA02251|원가회계|3|A0|전공1단계|2020년 2학기|경과113|HBX01113|인적자원관리|3|B0|전공1단계|2020년 1학기|경과105|HBX01105|재무관리원론|3|A0|전공1단계|2020년 1학기|경과133|HBX01133|재무회계|3|A+|전공1단계|2020년 2학기|경영354|HCA02354|중급회계|3|A+|전공1단계|2022년 1학기|경영375|HCA02375|중급회계2|3|A0|전공1단계|2022년 1학기|경영451|HCA02451|회계감사|3|A+|전공1단계|2019년 2학기|경과104|HBX01104|회계원리|3|A+|2022년 2학기|기사167|KMD02167|MJ사회봉사|3|2022년 2학기|경과127|HBX01127|국제경영학|3|2022년 2학기|경상202|HBW01202|국제금융론|3|2022년 2학기|균사172|KMM02172|기업가정신과창업(KCU)|3|2022년 2학기|교필108|KMA02108|영어회화1|1|2022년 2학기|경영253|HCA02253|재무분석|3|";

		//when
		ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);

		//then
		assertThat(parsingInformation)
			.extracting("studentName", "studentNumber", "major", "subMajor", "associatedMajor", "studentCategory")
			.contains("이아현", "60191000", "경영학과", null, null, StudentCategory.NORMAL);
	}

	@DisplayName("복수전공을 할 경우 StudentCategory는 DOUBLE MAJOR이다.")
	@Test
	void test() {
	    //given
		String parsingText = "출력일자 :  2022/11/14|1/1"
			+ "|사회과학대학 정치외교학과, 복수전공 - 경제학과, 이인구(60161000), 현학적 - 재학, 이수 - 7, 입학 - 신입학(2015/03/02)"
			+ "|토익 - 690, 영어교과목면제 - 면제없음, 최종학적변동 - 일반복학(2021/01/06)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 15, 핵심교양 12, 학문기초교양 9, 일반교양 14, 전공 33, 복수전공 27, 연계전공 9, 부전공 0, 교직 0, 자유선택 3"
			+ "|총 취득학점 - 122, 총점 - 414, 평균평점 - 3.51"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양 (구 필수교양)|2016년 2학기|교필105|KMA02105|발표와토의|3|A0|공통교양 (구 필수교양)|2015년 1학기|교필100|KMA02100|성경개론|2|A+|공통교양 (구 필수교양)|2015년 1학기|교필123|KMA02123|영어3|2|A0|공통교양 (구 필수교양)|2019년 1학기|교필124|KMA02124|영어4|2|A+|공통교양 (구 필수교양)|2015년 1학기|교필125|KMA02125|영어회화3|1|A+|공통교양 (구 필수교양)|2016년 2학기|교필126|KMA02126|영어회화4|1|B+|공통교양 (구 필수교양)|2019년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2019년 2학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2021년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2021년 2학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2016년 2학기|교필102|KMA02102|현대사회와기독교윤리|2|A0|핵심교양 (구 선택교양)|2016년 2학기|교선114|KMA02114|민주주의와현대사회|3|A+|핵심교양 (구 선택교양)|2021년 2학기|교선132|KMA02132|예술과창조성|3|D+|핵심교양 (구 선택교양)|2021년 1학기|교선127|KMA02127|창업입문|3|C+|핵심교양 (구 선택교양)|2019년 1학기|교선111|KMA02111|한국근현대사의이해|3|A+|학문기초교양 (구 기초교양)|2016년 2학기|기사123|KMD02123|경제학원론|3|B+|학문기초교양 (구 기초교양)|2016년 2학기|기사111|KMD02111|동북아정세론|3|A+|학문기초교양 (구 기초교양)|2019년 2학기|기사116|KMD02116|청소년지도학|3|A0|일반교양 (구 균형교양)|2015년 1학기|균여219|KMO02119|교양바둑|2|A0|일반교양 (구 균형교양)|2022년 1학기|균여115|KMO02115|교양축구|2|B+|6|일반교양 (구 균형교양)|2016년 2학기|균기113|KMJ02113|기독교와상담|2|A+|일반교양 (구 균형교양)|2022년 1학기|기사114|KMD02114|미시경제학원론|3|A0|4|일반교양 (구 균형교양)|2015년 1학기|기사110|KMD02110|시민사회와행정|3|C+|일반교양 (구 균형교양)|2015년 1학기|균사133|KMM02133|진로선택과대학생활|2|P|전공1단계|2019년 2학기|정외212|HBB01212|국제정치론|3|A+|전공1단계|2022년 1학기|정외100|HBB01100|글로벌위기시대의한국과세계정치|3|B0|1|전공1단계|2021년 1학기|정외333|HBB01333|미국정치론|3|B+|전공1단계|2021년 2학기|정외242|HBB01242|비교정치론|3|B0|전공1단계|2019년 1학기|정외224|HBB01224|여론과정치참여|3|B+|전공1단계|2019년 1학기|정외257|HBB01257|여성정치론|3|B+|전공1단계|2019년 1학기|정외351|HBB01351|유럽연합정치학입문|3|B+|전공1단계|2015년 1학기|정외101|HBB01101|정치학개론|3|A0|전공1단계|2021년 2학기|정외256|HBB01256|정치학인턴쉽|3|A0|전공1단계|2019년 1학기|정외272|HBB01272|한반도와세계정치|3|A0|전공1단계|2019년 2학기|정외315|HBB01315|현대정치사상|3|B+|복수전공|2022년 1학기|경제104|HBE01493|Stata를이용한경제데이터분석론|3|B+|3|복수전공|2019년 2학기|경제204|HBE01204|경제수학|3|B+|복수전공|2019년 2학기|경제103|HBE01103|국민경제론|3|B+|복수전공|2021년 1학기|경제437|HBE01437|금융상품론|3|B+|복수전공|2021년 2학기|경제439|HBE01439|금융혁신과경제|3|B0|복수전공|2022년 1학기|경제413|HBE01413|기업금융론|3|B0|2|복수전공|2021년 1학기|경제327|HBE01327|벤처기업론|3|D+|복수전공|2021년 1학기|경제102|HBE01102|시장및기업이론|3|C+|복수전공|2021년 2학기|경제240|HBE01240|창업과혁신|3|C0|연계전공|2022년 1학기|혁신103|HGA01103|사회문제Prototype개발|3|A0|5|연계전공|2021년 1학기|혁신104|HGA01104|사회문제융합과제의실천|3|B+|연계전공|2021년 2학기|혁신102|HGA01102|사회문제의발견과융합|3|B+|자유선택|2018년 동계계절|균컴113|KMQ01113|SNS로톡하고빅데이터로놀아보자(KCU)|3|B+|2022년 1학기|경제104|HBE01493|Stata를이용한경제데이터분석론|3|3|2022년 1학기|균여115|KMO02115|교양축구|2|6|2022년 1학기|정외100|HBB01100|글로벌위기시대의한국과세계정치|3|1|2022년 1학기|경제413|HBE01413|기업금융론|3|2|2022년 1학기|기사114|KMD02114|미시경제학원론|3|4|2022년 1학기|혁신103|HGA01103|사회문제Prototype개발|3|5|일반교양 (구 균형교양)|2015년 1학기|균명102|KMR02102|성공학특강|2|N|";
	    //when
		ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
		//then
		assertThat(parsingInformation)
			.extracting("studentName", "studentNumber", "major", "subMajor", "associatedMajor", "studentCategory")
			.contains("이인구", "60161000", "정치외교학과", "경제학과", null, StudentCategory.DOUBLE_MAJOR);

	}

	@DisplayName("전과을 할 경우 StudentCategory는 CHANGE_MAJOR이다.")
	@Test
	void 전과생_확인() {
		//given
		String parsingText = "출력일자 :  2023/12/08|1/1"
			+ "|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 나경호(60202455), 현학적 - 재학, 이수 - 3, 입학 - 신입학(2020/03/02)"
			+ "|토익 - 715, 영어교과목면제 - 면제없음, 최종학적변동 - 2/1전과(2023/01/27), 전과내역 - 기계공학과"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 16.5, 핵심교양 6, 학문기초교양 6, 일반교양 12, 전공 9, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 7"
			+ "|총 취득학점 - 56.5, 총점 - 230.5, 평균평점 - 4.43"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2020년 2학기|교필141|KMA02141|4차산업혁명과미래사회진로선택|2|P|공통교양|2020년 2학기|교필122|KMA02122|기독교와문화|2|A+|공통교양|2023년 1학기|교필105|KMA02105|발표와토의|3|A0|공통교양|2020년 1학기|교필127|KMA00101|성서와인간이해|2|A+|공통교양|2020년 1학기|교필123|KMA02123|영어3|2|A+|공통교양|2020년 2학기|교필124|KMA02124|영어4|2|A+|공통교양|2020년 1학기|교필125|KMA02125|영어회화3|1|A0|공통교양|2020년 2학기|교필126|KMA02126|영어회화4|1|A+|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2023년 1학기|교선128|KMA02128|글로벌문화|3|A0|핵심교양|2023년 1학기|교선114|KMA02114|민주주의와현대사회|3|A+|학문기초교양|2020년 1학기|기자111|KME02111|물리학1|3|A+|학문기초교양|2020년 1학기|기자101|KME02101|미적분학1|3|A+|일반교양|2020년 2학기|기자112|KME02112|물리학2|3|A+|일반교양|2020년 1학기|기자113|KME02113|물리학실험1|1|A+|일반교양|2020년 2학기|기자114|KME02114|물리학실험2|1|A+|일반교양|2020년 2학기|기자102|KME02102|미적분학2|3|A+|일반교양|2020년 1학기|기자121|KME02121|일반화학|3|A+|일반교양|2020년 1학기|기자122|KME02122|일반화학실험|1|A+|전공1단계|2023년 1학기|데테202|HED01202|R통계분석|3|A+|전공1단계|2023년 1학기|융소102|HEB01102|기초프로그래밍|3|A+|전공1단계|2023년 1학기|데테201|HED01201|자료구조|3|A+|자유선택|2020년 2학기|공과100|JEA00100|공학입문설계|3|A+|자유선택|2020년 1학기|기계207|JEP01207|기계신입생세미나|1|P|자유선택|2020년 2학기|기계209|JEP02209|정역학|3|A+|";

		//when
		ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);

		//then
		assertThat(parsingInformation)
			.extracting("studentName", "studentNumber", "major", "subMajor", "associatedMajor", "studentCategory")
			.contains("나경호", "60202455", "데이터테크놀로지전공", null, null, StudentCategory.CHANGE_MAJOR);
	}


	@DisplayName("재수강, F학점, Non pass인 경우 수강 정보를 추출하지 않는다.")
	@Test
	void RFN인경우_수강정보제외() {
	    //given
	    String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 정지환(60181666), 현학적 - 재학, 이수 - 7, 입학 - 신입학(2018/03/02)"
			+ "|토익 - 570, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2022/07/15)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 17, 핵심교양 12, 학문기초교양 18, 일반교양 5, 전공 63, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 3"
			+ "|총 취득학점 - 118, 총점 - 436, 평균평점 - 3.82"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양|2022년 동계계절|교필104|KMA02104|글쓰기|3|C0|공통교양|2020년 1학기|교필122|KMA02122|기독교와문화|2|B0|공통교양|2019년 2학기|교필127|KMA00101|성서와인간이해|2|B+|공통교양|2019년 하계계절|교필106|KMA02106|영어1|2|B+|2|공통교양|2019년 2학기|교필107|KMA02107|영어2|2|B+|공통교양|2022년 동계계절|교필108|KMA02108|영어회화1|1|B+|3|공통교양|2023년 1학기|교필109|KMA02109|영어회화2|1|A0|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2022년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2022년 2학기|교선130|KMA02130|고전으로읽는인문학|3|A0|핵심교양|2019년 2학기|교선113|KMA02113|세계화와사회변화|3|B+|핵심교양|2018년 1학기|교선135|KMA02135|우주,생명,마음|3|C+|핵심교양|2020년 2학기|교선111|KMA02111|한국근현대사의이해|3|A+|학문기초교양|2019년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|B+|학문기초교양|2022년 2학기|기사123|KMD02123|경제학원론|3|A+|학문기초교양|2022년 2학기|기사103|KMD02103|대중문화와매스컴|3|A0|학문기초교양|2019년 1학기|기자107|KME02107|선형대수학개론|3|B0|6|학문기초교양|2019년 하계계절|기인107|KMB02107|인간심리의이해|3|B+|5|학문기초교양|2020년 2학기|기자106|KME02106|통계학개론|3|A+|일반교양|2022년 2학기|균여111|KMO02111|교양볼링|2|B+|일반교양|2020년 1학기|기컴118|KMI02118|엑셀기초및실무활용|3|B+|4|전공1단계|2019년 1학기|데테202|HED01202|R통계분석|3|A0|전공1단계|2022년 2학기|데테314|HED01314|게임프로그래밍|3|A+|전공1단계|2020년 1학기|데테316|HED01316|고급웹프로그래밍|3|A0|전공1단계|2019년 2학기|데테206|HED01206|기초웹프로그래밍|3|B+|전공1단계|2019년 1학기|융소102|HEB01102|기초프로그래밍|3|B+|1|전공1단계|2019년 2학기|융소105|HEB01105|기초프로그래밍2|3|C+|전공1단계|2019년 2학기|데테318|HED01203|데이터베이스|3|A0|전공1단계|2023년 1학기|데테317|HED01317|데이터베이스프로젝트|3|A+|전공1단계|2023년 1학기|데테407|HED01407|딥러닝|3|B+|전공1단계|2023년 1학기|데테319|HED01318|모바일컴퓨팅|3|A+|전공1단계|2020년 2학기|데테403|HED01403|블록체인기초|3|A+|전공1단계|2023년 1학기|데테404|HED01404|빅데이터기술특론1|3|B0|전공1단계|2022년 2학기|데테306|HED01306|빅데이터프로그래밍|3|A0|전공1단계|2020년 1학기|데테301|HED01301|소프트웨어공학|3|A+|전공1단계|2020년 2학기|데테209|HED01307|알고리즘|3|A+|전공1단계|2020년 2학기|데테303|HED01303|운영체제|3|A+|전공1단계|2020년 1학기|데테315|HED01315|인공지능|3|B+|전공1단계|2019년 1학기|데테201|HED01201|자료구조|3|A0|전공1단계|2023년 1학기|데테413|HED01413|캡스톤디자인|3|A+|전공1단계|2020년 1학기|데테309|HED01309|컴퓨터아키텍쳐|3|A0|전공1단계|2020년 2학기|데테313|HED01313|컴퓨터통신|3|A+|자유선택|2023년 1학기|교선136|KMA02136|SW프로그래밍입문|3|A+|공통교양|2019년 2학기|교필101|KMA02101|채플|0.5|N|공통교양|2018년 1학기|교필106|KMA02106|영어1|0|R|2|공통교양|2018년 1학기|교필108|KMA02108|영어회화1|0|R|3|공통교양|2020년 1학기|교필108|KMA02108|영어회화1|0|R|3|학문기초교양|2018년 1학기|기자107|KME02107|선형대수학개론|0|R|6|학문기초교양|2018년 1학기|기인107|KMB02107|인간심리의이해|0|R|5|일반교양|2018년 1학기|기컴118|KMI02118|엑셀기초및실무활용|0|R|4|전공1단계|2018년 1학기|융소102|HEB01102|기초프로그래밍|0|R|1|";
	    //when
		ParsingInformation parsingInformation = ParsingInformation.parsing(parsingText);
		//then
		assertThat(parsingInformation)
			.extracting("studentName", "studentNumber", "major", "subMajor", "associatedMajor", "studentCategory")
			.contains("정지환", "60181666", "데이터테크놀로지전공", null, null, StudentCategory.NORMAL);
	    assertThat(parsingInformation.getTakenLectureInformation()).hasSize(46);
	}

}
