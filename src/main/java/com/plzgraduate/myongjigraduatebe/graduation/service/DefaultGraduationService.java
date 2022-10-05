package com.plzgraduate.myongjigraduatebe.graduation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BasicInfo;
import com.plzgraduate.myongjigraduatebe.graduation.dto.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.Transcript;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRepository;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultGraduationService implements GraduationService {

  private final GraduationRepository graduationRepository;
  private final DepartmentRepository departmentRepository;
  private final LectureRepository lectureRepository;

  @Transactional(readOnly = true)
  @Override
  public GraduationResult assess(String parsingText) {
    // String parsingText = PdfParser.parseString(request);
    // String parsingText = PdfParser.getSampleString();
    Transcript transcript = textToTranscript(parsingText); // PDF Parsing String -> 객체 변환코드

    Department department = departmentRepository
        .findByName(transcript.getDepartmentName())
        .orElseThrow(() -> new IllegalArgumentException(transcript.getDepartmentName() + " 이름의 학과를 찾을 수 없습니다."));

    return assessGraduation(transcript, department);
  }

  private GraduationResult assessGraduation(
      Transcript transcript,
      Department department
  ) {
    GraduationRequirement requirement = graduationRepository
        .findRequirementByDepartmentAndEntryYear(
            department,
            transcript.getEntryYear()
        )
        .orElseThrow(() -> new IllegalArgumentException("졸업 요건을 조회할 수 없습니다."));
    Map<LectureCategory, List<GraduationLecture>> categoryToLectures = new HashMap<>(LectureCategory.values().length);
    List<GraduationLecture> graduationLectures = graduationRepository.findAllGraduationLecture(requirement);
    List<Lecture> takenLectures = lectureRepository.findAllByLectureCodeIsIn(transcript.getTakenLectureCodes());
    List<GraduationLecture> takenGraduationLectures = graduationRepository.findAllByGraduationRequirementAndLectureIsIn(
        requirement,
        takenLectures
    );

    for (GraduationLecture graduationLecture : graduationLectures) {
      LectureCategory category = graduationLecture.getLectureCategory();
      if (!categoryToLectures.containsKey(category)) {
        categoryToLectures.put(category, new ArrayList<>());
      }
      if (graduationLecture.getLectureCategory() == LectureCategory.COMMON_CULTURE_ENGLISH &&
          !transcript
              .getEnglishLevel()
              .getLectureCodeList()
              .contains(graduationLecture
                            .getLecture()
                            .getLectureCode())) {
        continue;
      }
      List<GraduationLecture> lectures = categoryToLectures.get(category);
      lectures.add(graduationLecture);
    }

    boolean creditResult = requirement.checkCredit(transcript);
    Lecture chapelLecture = categoryToLectures
        .get(LectureCategory.COMMON_CULTURE_CHAPEL)
        .get(0)
        .getLecture();
    long countChapel = transcript
        .getTakenLectureCodes()
        .stream()
        .filter(lc -> lc
            .getCode()
            .equals(chapelLecture.getCode()))
        .count();
    ChapelResult chapelResult = new ChapelResult(LectureCategory.COMMON_CULTURE_CHAPEL.getTotalCredit(), (int)countChapel);

    DetailGraduationResult commonCultureResult = assessByLectureCategories(
        requirement,
        categoryToLectures,
        takenGraduationLectures,
        LectureCategory.findAllCommonCategory()
    );

    DetailGraduationResult coreCultureResult = assessByLectureCategories(
        requirement,
        categoryToLectures,
        takenGraduationLectures,
        LectureCategory.findAllCoreCategory()
    );
    DetailGraduationResult basicAcademicalCultureResult = assessByLectureCategories(
        requirement,
        categoryToLectures,
        takenGraduationLectures,
        List.of(LectureCategory.BASIC_ACADEMICAL_CULTURE)
    );
    DetailGraduationResult majorResult = assessByLectureCategories(
        requirement,
        categoryToLectures,
        takenGraduationLectures,
        List.of(LectureCategory.MAJOR)
    );
    DetailGraduationResult normalCultureResult = assessNormalCulture(
        requirement,
        transcript,
        List.of(
            commonCultureResult,
            coreCultureResult,
            basicAcademicalCultureResult
        )
    );
    DetailGraduationResult freeElectiveResult = assessFreeElective(
        requirement,
        transcript,
        List.of(
            majorResult,
            normalCultureResult
        )
    );

    boolean graduated = checkGraduated(
        creditResult,
        chapelResult,
        commonCultureResult,
        coreCultureResult,
        basicAcademicalCultureResult,
        majorResult,
        normalCultureResult,
        freeElectiveResult
    );

    BasicInfo basicInfo = BasicInfo
        .builder()
        .name(transcript.getStudentName())
        .studentNumber(transcript.getStudentNumber())
        .totalCredit(requirement.getTotalCredit())
        .takenCredit((int)(transcript.getTakenCredit() - (0.5 * chapelResult.getTakenCount())))
        .department(department)
        .build();

    return GraduationResult
        .builder()
        .basicInfo(basicInfo)
        .isGraduated(graduated)
        .chapelResult(chapelResult)
        .commonCulture(commonCultureResult)
        .coreCulture(coreCultureResult)
        .basicAcademicalCulture(basicAcademicalCultureResult)
        .major(majorResult)
        .normalCulture(normalCultureResult)
        .freeElective(freeElectiveResult)
        .build();
  }

  private boolean checkGraduated(
      boolean creditResult,
      ChapelResult chapelResult,
      DetailGraduationResult commonCultureResult,
      DetailGraduationResult coreCultureResult,
      DetailGraduationResult basicAcademicalCultureResult,
      DetailGraduationResult majorResult,
      DetailGraduationResult normalCultureResult,
      DetailGraduationResult freeElectiveResult
  ) {
    return creditResult &&
        chapelResult.isCompleted() &&
        commonCultureResult.isCompleted() &&
        coreCultureResult.isCompleted() &&
        basicAcademicalCultureResult.isCompleted() &&
        majorResult.isCompleted() &&
        normalCultureResult.isCompleted() &&
        freeElectiveResult.isCompleted();
  }

  private DetailGraduationResult assessFreeElective(
      GraduationRequirement requirement,
      Transcript transcript,
      List<DetailGraduationResult> graduationResults
  ) {
    int takenCredit = transcript.getFreeElectiveCredit();

    for (DetailGraduationResult result : graduationResults) {
      if (result.getTotalCredit() < result.getTakenCredit()) {
        takenCredit += result.getTakenCredit() - result.getTotalCredit();
      }
    }

    return DetailGraduationResult
        .builder()
        .totalCredit(requirement.getFreeElectiveCredit())
        .takenCredit(takenCredit)
        .detailCategory(List.of())
        .build();
  }

  private DetailGraduationResult assessNormalCulture(
      GraduationRequirement requirement,
      Transcript transcript,
      List<DetailGraduationResult> graduationResults
  ) {
    int takenNormalCredit = transcript.getNormalCultureCredit();

    for (DetailGraduationResult result : graduationResults) {
      if (result.getTotalCredit() < result.getTakenCredit()) {
        takenNormalCredit += result.getTakenCredit() - result.getTotalCredit();
      }
    }

    return DetailGraduationResult
        .builder()
        .totalCredit(requirement.getNormalCultureCredit())
        .takenCredit(takenNormalCredit)
        .detailCategory(List.of())
        .build();
  }

  private DetailGraduationResult assessByLectureCategories(
      GraduationRequirement requirement,
      Map<LectureCategory, List<GraduationLecture>> categoryToLectures,
      List<GraduationLecture> takenGraduationLectures,
      List<LectureCategory> categories
  ) {
    int takenCredit = 0;
    List<DetailCategoryResult> detailCategory = new ArrayList<>();

    for (LectureCategory category : categories) {
      int totalCredit = categories.size() != 1 ? category.getTotalCredit() : requirement.getTotalCategoryCredit(category);
      DetailCategoryResult categoryResult = new DetailCategoryResult(category, totalCredit);
      List<GraduationLecture> graduationLectures = categoryToLectures.get(category);
      categoryResult.addAllRequiredLectures(graduationLectures);

      takenGraduationLectures.forEach(graduationLecture -> {
        if (category != graduationLecture.getLectureCategory()) {
          return;
        }

        categoryResult.addTakenLecture(graduationLecture);
      });

      categoryResult.checkCompleted();
      takenCredit += categoryResult.getTakenCredits();
      detailCategory.add(categoryResult);
    }
    LectureCategory category;
    if (categories.size() == 1) {
      category = categories.get(0);
    } else if (categories.get(0) == LectureCategory
        .findAllCommonCategory()
        .get(0)) {
      category = LectureCategory.COMMON_CULTURE;
    } else {
      category = LectureCategory.CORE_CULTURE;
    }

    return DetailGraduationResult
        .builder()
        .totalCredit(requirement.getTotalCategoryCredit(category))
        .takenCredit(takenCredit)
        .detailCategory(detailCategory)
        .build();
  }

  private Transcript textToTranscript(String parsingText) {
    try {
      String[] splitText = parsingText.split("\\|");

      String[] header = splitText[0].split(",");

      String[] departmentInfo = header[0].split(" ");
      String departmentName = departmentInfo[departmentInfo.length - 1];

      String[] studentInfo = header[1].split("\\(");
      String studentName = studentInfo[0];
      String studentNumber = studentInfo[1].substring(0, 8);

      EnglishLevel englishLevel = EnglishLevel.parse(splitText[1]);

      String[] creditInfo = splitText[4].split(",");
      double commonCultureCredit = Double.parseDouble(creditInfo[0].split(" ")[1]);
      int coreCultureCredit = Integer.parseInt(creditInfo[1].split(" ")[2]);
      int basicAcademicalCultureCredit = Integer.parseInt(creditInfo[2].split(" ")[2]);
      int normalCultureCredit = Integer.parseInt(creditInfo[3].split(" ")[2]);
      int majorCredit = Integer.parseInt(creditInfo[4].split(" ")[2]);
      int freeElectiveCredit = Integer.parseInt(creditInfo[9].split(" ")[2]);
      double takenCredit = Double.parseDouble(splitText[5].split(",")[0].split(" ")[3]);
      List<LectureCode> takenLectureCodes = getTakenLectureCodes(splitText);

      return Transcript
          .builder()
          .departmentName(departmentName)
          .studentName(studentName)
          .studentNumber(studentNumber)
          .englishLevel(englishLevel)
          .commonCultureCredit(commonCultureCredit)
          .coreCultureCredit(coreCultureCredit)
          .basicAcademicalCultureCredit(basicAcademicalCultureCredit)
          .normalCultureCredit(normalCultureCredit)
          .majorCredit(majorCredit)
          .freeElectiveCredit(freeElectiveCredit)
          .takenCredit(takenCredit)
          .takenLectureCodes(takenLectureCodes)
          .build();

    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      throw new IllegalArgumentException("PDF 형식이 잘못 되었습니다.");
    }
  }

  private List<LectureCode> getTakenLectureCodes(
      String[] splitText
  ) {
    int countTakenLecture = (splitText.length - 14) / 8;
    List<LectureCode> takenLectureCods = new ArrayList<>(countTakenLecture);
    for (int i = 14; i < splitText.length; i += 7) {
      String code = splitText[i + 3];
      if (i + 7 < splitText.length && Character.isDigit(splitText[i + 7].charAt(0))) {
        i++;
      }

      takenLectureCods.add(new LectureCode(code));
    }

    return takenLectureCods;
  }
}
