package com.plzgraduate.myongjigraduatebe.common.utils;

import java.io.IOException;

import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationRequest;

public class PdfParser {
  public static String parseString(GraduationRequest request) {
    try {
      return formatParsing(request);
    } catch (IOException e) {
      throw new IllegalArgumentException("PDF 형식이 잘못되었습니다");
    }
  }

  private static String formatParsing(GraduationRequest request) throws IOException {
    com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(request
                                                                          .getFile()
                                                                          .getInputStream());
    com.aspose.pdf.TableAbsorber absorber = new com.aspose.pdf.TableAbsorber();
    StringBuilder sb = new StringBuilder();
    for (com.aspose.pdf.Page page : pdfDocument.getPages()) {
      absorber.visit(page);
      for (com.aspose.pdf.AbsorbedTable table : absorber.getTableList()) {
        for (com.aspose.pdf.AbsorbedRow row : table.getRowList()) {
          for (com.aspose.pdf.AbsorbedCell cell : row.getCellList()) {
            for (com.aspose.pdf.TextFragment fragment : cell.getTextFragments()) {
              for (com.aspose.pdf.TextSegment seg : fragment.getSegments()) {
                sb.append(seg.getText());
              }
              sb.append("|");
            }

          }
        }
      }
    }
    return sb.toString();
  }

  public static String getSampleString() {
    return "ICT융합대학 융합소프트웨어학부 응용소프트웨어전공, 박수환(60191656), 현학적 - 재학, 이수 - 3, 입학 - 신입학(2019/03/04)|토익 - 460, 영어교과목면제 - 면제없음, 최종학적변동 - 군입대복학(2022/01/10)|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0|공통교양 13.5, 핵심교양 9, 학문기초교양 6, 일반교양 5, 전공 18, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0|총 취득학점 - 51.5, 총점 - 153, 평균평점 - 3.12|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|공통교양 (구 필수교양)|2019년 2학기|교필137|KMA02137|4차산업혁명시대의진로선택|2|P|공통교양 (구 필수교양)|2019년 2학기|교필105|KMA02105|발표와토의|3|C+|공통교양 (구 필수교양)|2019년 2학기|교필127|KMA00101|성서와인간이해|2|B0|공통교양 (구 필수교양)|2022년 1학기|교필106|KMA02106|영어1|2|B+|공통교양 (구 필수교양)|2022년 하계계절|교필108|KMA02108|영어회화1|1|A0|공통교양 (구 필수교양)|2019년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2019년 2학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2022년 1학기|교필101|KMA02101|채플|0.5|P|공통교양 (구 필수교양)|2019년 1학기|교필102|KMA02102|현대사회와기독교윤리|2|B+|핵심교양 (구 선택교양)|2019년 2학기|교선114|KMA02114|민주주의와현대사회|3|C+|1|핵심교양 (구 선택교양)|2019년 1학기|교선112|KMA02112|역사와문명|3|B0|핵심교양 (구 선택교양)|2019년 1학기|교선135|KMA02135|우주,생명,마음|3|B0|학문기초교양 (구 기초교양)|2022년 1학기|기사133|KMD02133|ICT비즈니스와경영|3|A0|학문기초교양 (구 기초교양)|2022년 1학기|기사103|KMD02103|대중문화와매스컴|3|B0|일반교양 (구 균형교양)|2019년 1학기|균명102|KMR02102|성공학특강|2|P|일반교양 (구 균형교양)|2019년 2학기|균인106|KMK02106|영화로보는역사|3|C+|전공1단계|2022년 1학기|응소204|HEC01204|DB설계및구현1|3|A0|전공1단계|2019년 2학기|융소103|HEB01103|객체지향적사고와프로그래밍|3|B0|전공1단계|2022년 1학기|응소208|HEC01208|데이터구조와알고리즘1|3|A0|전공1단계|2022년 1학기|응소211|HEC01211|웹프로그래밍1|3|A0|전공1단계|2019년 1학기|융소101|HEB01101|절차적사고와프로그래밍|3|B+|전공1단계|2022년 1학기|응소202|HEC01202|패턴중심사고와프로그래밍|3|A0|핵심교양 (구 선택교양)|2019년 1학기|교선114|KMA02114|민주주의와현대사회|3|F  ***|1|";
  }
}
