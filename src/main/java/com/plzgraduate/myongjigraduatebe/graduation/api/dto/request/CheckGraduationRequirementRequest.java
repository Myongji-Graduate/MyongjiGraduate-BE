package com.plzgraduate.myongjigraduatebe.graduation.api.dto.request;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckGraduationRequirementRequest {

	@NotBlank(message = "영어 레벨을 입력해 주세요.")
	@Schema(name = "engLv", example = "ENG12")
	private String engLv;

	@NotBlank(message = "한국어 레벨을 입력해 주세요.")
	@Schema(name = "korLv", example = "FREE")
	private String korLv;

	@NotNull(message = "parsingText는 null 값이 될 수 없습니다.")
	@Schema(
		name = "parsingText",
		example = "출력일자 : 2024/11/20|1/1|ICT융합대학 융합소프트웨어학부 데이터테크놀로지전공, 김보겸(60211648), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2021/03/02)|토익 - 535, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2023/01/09)|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 융합전공 0, 부전공 0, 자유선택 0|공통교양 17, 핵심교양 12, 학문기초교양 15, 일반교양 14, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0|총 취득학점 - 108, 총점 - 441.5, 평균평점 - 4.33|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양|2023년 2학기|교필141|KMA02141|4차산업혁명과미래사회진로선택|2|P|공통교양|2021년 2학기|교필105|KMA02105|발표와토의|3|A0|공통교양|2021년 2학기|교필127|KMA00101|성서와인간이해|2|A0|공통교양|2021년 1학기|교필106|KMA02106|영어1|2|A+|공통교양|2021년 2학기|교필107|KMA02107|영어2|2|A+|공통교양|2021년 1학기|교필108|KMA02108|영어회화1|1|A+|공통교양|2021년 2학기|교필109|KMA02109|영어회화2|1|A0|공통교양|2021년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2021년 2학기|교필101|KMA02101|채플|0.5|P|공통교양|2022년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2023년 1학기|교필101|KMA02101|채플|0.5|P|공통교양|2021년 2학기|교필102|KMA02102|현대사회와기독교윤리|2|A0|핵심교양|2022년 1학기|교선128|KMA02128|글로벌문화|3|A+|핵심교양|2021년 1학기|교선114|KMA02114|민주주의와현대사회|3|A+|핵심교양|2021년 2학기|교선112|KMA02112|역사와문명|3|A0|핵심교양|2021년 1학기|교선135|KMA02135|우주,생명,마음|3|B+|학문기초교양|2023년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|A0|학문기초교양|2023년 1학기|기사134|KMD02134|마케팅과ICT융합기술|3|A0|학문기초교양|2021년 1학기|기인107|KMB02107|인간심리의이해|3|A+|학문기초교양|2021년 2학기|기사135|KMD02135|저작권과소프트웨어|3|A+|학문기초교양|2022년 1학기|기컴112|KMI02112|컴퓨터논리의이해|3|B+|일반교양|2022년 1학기|균인131|KMK02131|사랑의인문학(KCU)|3|A0|일반교양|2021년 1학기|기컴125|KMI02125|생활속의스마트IT(KCU)|3|A+|일반교양|2024년 1학기|기문227|KMC02227|인류문명과기록문화로의여행|3|A+|일반교양|2024년 1학기|균사181|KMM02181|자기경영과실전취업준비|2|A+|일반교양|2024년 1학기|기컴126|KMI02126|파이썬프로그래밍입문|3|A+|전공1단계|2022년 1학기|데테202|HED01202|R통계분석|3|A+|전공1단계|2022년 1학기|데테316|HED01316|고급웹프로그래밍|3|A+|전공1단계|2023년 2학기|데테206|HED01206|기초웹프로그래밍|3|A+|전공1단계|2021년 1학기|융소102|HEB01102|기초프로그래밍|3|A+|전공1단계|2021년 2학기|융소105|HEB01105|기초프로그래밍2|3|A+|전공1단계|2023년 1학기|데테318|HED01203|데이터베이스|3|A+|전공1단계|2024년 1학기|데테319|HED01318|모바일컴퓨팅|3|A+|전공1단계|2023년 2학기|데테403|HED01403|블록체인기초|3|A+|전공1단계|2024년 1학기|데테404|HED01404|빅데이터기술특론1|3|A+|전공1단계|2023년 1학기|데테301|HED01301|소프트웨어공학|3|A+|전공1단계|2023년 2학기|데테209|HED01307|알고리즘|3|A+|전공1단계|2023년 2학기|데테303|HED01303|운영체제|3|A+|전공1단계|2023년 2학기|데테311|HED01311|자기주도학습|2|P|전공1단계|2022년 1학기|데테201|HED01201|자료구조|3|A+|전공1단계|2024년 1학기|데테413|HED01413|캡스톤디자인|3|A+|전공1단계|2023년 1학기|데테309|HED01309|컴퓨터아키텍쳐|3|A0|전공1단계|2023년 2학기|데테313|HED01313|컴퓨터통신|3|A+|"
	)
	private String parsingText;

	@Builder
	public CheckGraduationRequirementRequest(String engLv, String korLv, String parsingText) {
		this.engLv = engLv;
		this.korLv = korLv;
		this.parsingText = parsingText;
	}

	public EnglishLevel getEngLv() {
		return EnglishLevel.valueOf(engLv);
	}

	public KoreanLevel getKorLv() {
		return KoreanLevel.valueOf(korLv);
	}
}
