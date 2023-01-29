package com.plzgraduate.myongjigraduatebe.graduation.service;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoCategory;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoLecturesResponse;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.dto.LectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRequirementRepository;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraduationRequirementServiceImpl implements GraduationRequirementService{

  private static final EnglishLevel DEFAULT_ENGLISH_LV = EnglishLevel.ENG12;

  private final GraduationRequirementRepository graduationRequirementRepository;
  private final GraduationLectureRepository graduationLectureRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  public BachelorInfoRequirementResponse getBachelorInfoRequirement(BachelorInfoRequest bachelorInfoRequest) {
    GraduationRequirement foundGraduationRequirement = graduationRequirementRepository.findByNameAndEntryYear(
        bachelorInfoRequest.getEntryYear(),
        bachelorInfoRequest.getDepartment(),
        DEFAULT_ENGLISH_LV
    );
    return BachelorInfoRequirementResponse.from(foundGraduationRequirement);
  }

  @Override
  public BachelorInfoLecturesResponse getBachelorInfoLectures(BachelorInfoRequest bachelorInfoRequest) {
    Optional<Department> byName = departmentRepository.findByName(bachelorInfoRequest.getDepartment());
    if (byName.isEmpty()) {
      throw new IllegalArgumentException("해당 학과가 존재하지 않습니다.");
    }
    List<GraduationLecture> graduationLectures = graduationLectureRepository.findAllByDepartmentAndEntryYearWithFetchJoin(
            byName.get(),
            bachelorInfoRequest.getEntryYear(),
            DEFAULT_ENGLISH_LV
    );

    Map<String, List<LectureResponse>> majorMap = new HashMap<>(){{
      put("전공필수", new ArrayList<>());
      put("전공선택", new ArrayList<>());
    }};
    Map<String, List<LectureResponse>> commonCultureMap = new HashMap<>();
    Map<String, List<LectureResponse>> coreCultureMap = new HashMap<>();
    Map<String, List<LectureResponse>> basicAcademicalCultureMap = new HashMap<>(){{
      put("학문기초교양", new ArrayList<>());
    }};

    classifyGraduationLectures(graduationLectures, majorMap, commonCultureMap, coreCultureMap, basicAcademicalCultureMap);

    return BachelorInfoLecturesResponse.of(
            createBachelorInfoCategory(majorMap),
            createBachelorInfoCategory(commonCultureMap),
            createBachelorInfoCategory(coreCultureMap),
            createBachelorInfoCategory(basicAcademicalCultureMap)
    );
  }

  private List<BachelorInfoCategory> createBachelorInfoCategory(Map<String, List<LectureResponse>> map) {
    List<BachelorInfoCategory> bachelorInfoCategoryList = new ArrayList<>();
    for( Map.Entry<String, List<LectureResponse>> entry: map.entrySet()) {
      bachelorInfoCategoryList.add(BachelorInfoCategory.of(entry.getKey(), entry.getValue()));
    }
    return bachelorInfoCategoryList;
  }

  private void classifyGraduationLectures(List<GraduationLecture> graduationLectures,
                                Map<String, List<LectureResponse>> majorMap,
                                Map<String, List<LectureResponse>> commonCultureMap,
                                Map<String, List<LectureResponse>> coreCultureMap,
                                Map<String, List<LectureResponse>> basicAcademicalCultureMap) {

    graduationLectures.forEach(graduationLecture -> {
      LectureCategory lectureCategory = graduationLecture
              .getLectureCategory();
      Lecture lecture = graduationLecture.getLecture();
      if(!lecture.isRevoked()){
        switch (lectureCategory.getCategory().name()) {
          case "MAJOR":
            divideMajorByMandatory(majorMap, graduationLecture, lectureCategory, lecture);
            break;
          case "COMMON_CULTURE":
            if(lecture.getLectureCode().getCode().equals("KMA00101") && graduationLecture.isMandatory()){
              lecture.setName(lecture.getName() + "(필수)");
            }
            commonCultureMap.computeIfAbsent(lectureCategory.getDetailCategory(), k -> new ArrayList<>())
                    .add(LectureResponse.from(lecture));
            break;
          case "CORE_CULTURE":
            coreCultureMap.computeIfAbsent(lectureCategory.getDetailCategory(), k -> new ArrayList<>())
                    .add(LectureResponse.from(lecture));
            break;
          case "BASIC_ACADEMICAL_CULTURE":
            basicAcademicalCultureMap.get("학문기초교양").add(LectureResponse.from(lecture));
        }
      }
    });
  }

  private void divideMajorByMandatory(Map<String, List<LectureResponse>> majorMap, GraduationLecture graduationLecture,
                                      LectureCategory lectureCategory, Lecture lecture) {
    if(graduationLecture.isMandatory()){
      majorMap.get("전공필수").add(LectureResponse.from(lecture));
    }
    else{
      String detailCategory = lectureCategory.getDetailCategory();
      if(detailCategory.charAt(detailCategory.length()-1)=='A') {
        String addedLectureName = lecture.getName() + "(선택필수)";
        lecture.setName(addedLectureName);
      }
      majorMap.get("전공선택").add(LectureResponse.from(lecture));
    }
  }
}
