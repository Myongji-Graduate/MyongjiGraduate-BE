package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.constraints.NotNull;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class ParsingTextRequest {

	@NotNull(message = "parsingText는 null값이 될 수 없습니다.")
	@Schema(name = "parsingText", example = "2023/12/08|1/1|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 나경호(60202455), 현학적 - 재학, 이수 - 3, 입학 - 신입학(2020/03/02)|토익 - 715, 영어교과목면제 - 면제없음, 최종학적변동 - 2/1전과(2023/01/27), 전과내역 - 기계공학과|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0|공통교양 16.5, 핵심교양 6, 학문기초교양 6, 일반교양 12, 전공 9, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 7|총 취득학점 - 56.5, 총점 - 230.5, 평균평점 - 4.43|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2020년 2학기|교필141|KMA02141|4차산업혁명과미래사회진로선택|2|P|공통교양|2020년 2학기|교필122|KMA02122|기독교와문화|2|A+|공통교양|2023년 1학기|교필105|KMA02105|발표와토의|3|A0|공통교양|2020년 1학기|교필127|KMA00101|성서와인간이해|2|A+|공통교양|2020년 1학기|교필123|KMA02123|영어3|2|A+|공통교양|2020년 2학기|교필124|KMA02124|영어4|2|A+|공통교양|2020년 1학기|교필125|KMA02125|영어회화3|1|A0|공통교양|2020년 2학기|교필126|KMA02126|영어회화4|1|A+|공통교양|2020년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2020년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|핵심교양|2023년 1학기|교선128|KMA02128|글로벌문화|3|A0|핵심교양|2023년 1학기|교선114|KMA02114|민주주의와현대사회|3|A+|학문기초교양|2020년 1학기|기자111|KME02111|물리학1|3|A+|학문기초교양|2020년 1학기|기자101|KME02101|미적분학1|3|A+|일반교양|2020년 2학기|기자112|KME02112|물리학2|3|A+|일반교양|2020년 1학기|기자113|KME02113|물리학실험1|1|A+|일반교양|2020년 2학기|기자114|KME02114|물리학실험2|1|A+|일반교양|2020년 2학기|기자102|KME02102|미적분학2|3|A+|일반교양|2020년 1학기|기자121|KME02121|일반화학|3|A+|일반교양|2020년 1학기|기자122|KME02122|일반화학실험|1|A+|전공1단계|2023년 1학기|데테202|HED01202|R통계분석|3|A+|전공1단계|2023년 1학기|융소102|HEB01102|기초프로그래밍|3|A+|전공1단계|2023년 1학기|데테201|HED01201|자료구조|3|A+|자유선택|2020년 2학기|공과100|JEA00100|공학입문설계|3|A+|자유선택|2020년 1학기|기계207|JEP01207|기계신입생세미나|1|P|자유선택|2020년 2학기|기계209|JEP02209|정역학|3|A+|")
	private String parsingText;

	@Builder
	private ParsingTextRequest(String parsingText) {
		this.parsingText = parsingText;
	}

	public ParsingTextCommand toCommand(Long userId) {
		return ParsingTextCommand.builder()
			.userId(userId)
			.parsingText(parsingText)
			.build();
	}
}
