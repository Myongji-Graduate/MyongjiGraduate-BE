package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.user.dto.EditedTakenLecture;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureDto;
import com.plzgraduate.myongjigraduatebe.user.dto.ShownTakenLectureDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.repository.TakenLectureRepository;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultTakenLectureService implements TakenLectureService {

  private static final String CHAPEL_CODE = "KMA02101";
  private static final String NORMAL_LECTURE_NAME = "일반교양";
  private final LectureRepository lectureRepository;
  private final TakenLectureRepository takenLectureRepository;
  private final UserRepository userRepository;

  @Override
  public void saveTakenLecture(
      AuthenticatedUser authUser,
      ParsingTextDto parsingTextDto
  ) {

    User user = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

    if (!(user
        .getStudentNumber()
        .equals(parsingTextDto.getStudentNumber()))) {
      throw new IllegalArgumentException("본인의 PDF 파일을 올려주세요.");
    }

    List<TakenLectureDto> takenLectureDtoList = parsingTextDto.getTakenLectureDto();
    List<TakenLecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user);

    List<Lecture> notUpdatedRevokedNormalLecture = getNotUpdatedRevokedNormalLecture(
        takenLectureDtoList);

    lectureRepository.saveAll(notUpdatedRevokedNormalLecture);

    List<TakenLecture> updatedTakenLectures = getUpdatedTakenLectures(
        user,
        takenLectureDtoList,
        previousLectures
    );
    takenLectureRepository.saveAll(updatedTakenLectures);
  }

  @Override
  public TakenLectureResponse showTakenLecture(AuthenticatedUser authUser) {
    User user = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

    List<ShownTakenLectureDto> shownTakenLectureDtoList =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(ShownTakenLectureDto::from)
            .collect(Collectors.toList());
    return TakenLectureResponse.of(shownTakenLectureDtoList);
  }

  @Override
  public void editTakenLecture(
      AuthenticatedUser authUser,
      EditedTakenLecture editedTakenLecture
  ) {
    User editUser = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

    deleteTakenLecture(editedTakenLecture.getDeletedTakenLectures());
    addTakenLecture(editUser, editedTakenLecture.getAddedTakenLectures());
  }

  @Override
  public List<TakenLecture> findAllByUserId(long id) {
    return takenLectureRepository.findAllByUserIdWithFetchJoin(id);
  }

  public void deleteTakenLecture(
      List<Long> deletedTakenLectures
  ) {
    if (deletedTakenLectures.size() != 0) {
      takenLectureRepository.deleteAllByIdIsIn(deletedTakenLectures);
    }
  }

  public void addTakenLecture(
      User user,
      List<Long> addedTakenLecture
  ) {
    if (addedTakenLecture.size() == 0) {
      return;
    }
    List<Lecture> addedLectures = addedTakenLecture
        .stream()
        .filter(id -> !checkNormalLectureToId(id))
        .map(this::getEditedLecture)
        .collect(Collectors.toList());
    Set<Lecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(TakenLecture::getLecture)
            .collect(Collectors.toSet());

    Lecture chapel = lectureRepository
        .findByLectureCode(LectureCode.of(CHAPEL_CODE))
        .orElseThrow(() -> new IllegalArgumentException("데이터베이스 에러"));

    List<TakenLecture> addedTakenLectures = addedLectures
        .stream()
        .filter(addedLecture -> containLectures(addedLecture, previousLectures, chapel))
        .map(addedLecture -> convertCustomTakenLecture(user, addedLecture))
        .collect(Collectors.toList());

    takenLectureRepository.saveAll(addedTakenLectures);
  }

  private List<TakenLecture> getUpdatedTakenLectures(
      User user,
      List<TakenLectureDto> takenLectureDtoList,
      List<TakenLecture> previousLectures
  ) {
    return takenLectureDtoList
        .stream()
        .map(takenLectureDto -> new TakenLecture(
            user,
            getLectureToLectureCode(takenLectureDto.getLectureCode()),
            takenLectureDto.getYear(),
            takenLectureDto.getSemester()
        ))
        .filter(takenLecture -> containTakenLectures(previousLectures, takenLecture))
        .collect(Collectors.toList());
  }

  private List<Lecture> getNotUpdatedRevokedNormalLecture(List<TakenLectureDto> takenLectureDtoList) {
    return takenLectureDtoList
        .stream()
        .filter(takenLectureDto -> checkNormalLectureToLectureCode(
            takenLectureDto.getCategory(),
            takenLectureDto.getLectureCode()
        ))
        .map(takenLectureDto -> new Lecture(
            takenLectureDto.getName(),
            takenLectureDto.getCredit(),
            takenLectureDto.getLectureCode(),
            null,
            false,
            true
        ))
        .collect(Collectors.toList());
  }

  private boolean checkNormalLectureToLectureCode(
      String category,
      LectureCode lectureCode
  ) {
    return (category.startsWith(NORMAL_LECTURE_NAME) && lectureRepository
        .findByLectureCode(lectureCode)
        .isEmpty());
  }

  private boolean checkNormalLectureToId(long id) {
    return (lectureRepository
        .findById(id)
        .isEmpty());
  }

  private Lecture getEditedLecture(long lectureId) {
    return lectureRepository
        .findById(lectureId)
        .orElseThrow(() -> new IllegalArgumentException("수정하고자 하는 과목이 존재하지 않습니다. 서비스 오류일 경우 채널톡으로 문의 부탁드립니다."));
  }

  private Lecture getLectureToLectureCode(LectureCode lectureCode) {
    return lectureRepository
        .findByLectureCode(lectureCode)
        .orElseThrow(() -> new IllegalArgumentException("본 서비스는 자연캠퍼스 과목이 있을 경우 지원되지 않습니다. 서비스 오류일 경우 채널톡으로 문의 부탁드립니다."));
  }

  private boolean containLectures(
      Lecture addTakenLecture,
      Set<Lecture> previousLectures,
      Lecture chapel
  ) {
    if (addTakenLecture.equals(chapel)) {
      return true;
    }
    return !previousLectures.contains(addTakenLecture);
  }

  private boolean containTakenLectures(
      List<TakenLecture> previousLectures,
      TakenLecture takenLecture
  ) {
    Lecture chapel = lectureRepository
        .findByLectureCode(LectureCode.of(CHAPEL_CODE))
        .orElseThrow(() -> new IllegalArgumentException("데이터베이스 에러"));
    List<TakenLecture> chapelTakenLectures = previousLectures
        .stream()
        .filter(previousLecture -> previousLecture
            .getLecture()
            .equals(chapel))
        .collect(Collectors.toList());
    if (takenLecture.getLecture().equals(chapel)) {
      long countTakenChapel  = chapelTakenLectures.stream()
          .filter(chapelTakenLecture -> takenLecture.getYear()
                          .equals(chapelTakenLecture.getYear()) &&
                          takenLecture.getSemester().equals(chapelTakenLecture.getSemester()) &&
                          takenLecture.equals(chapelTakenLecture)).count();
      return countTakenChapel == 0;
    }
    Set<TakenLecture> previousLecturesSet = new HashSet<>(previousLectures);
    return !previousLecturesSet.contains(takenLecture);
  }

  private TakenLecture convertCustomTakenLecture(
      User user,
      Lecture addLecture
  ) {
    return new TakenLecture(user, addLecture, null, null);
  }

}
