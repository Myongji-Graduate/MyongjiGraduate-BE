package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
  private final LectureRepository lectureRepository;
  private final TakenLectureRepository takenLectureRepository;
  private final UserRepository userRepository;

  private long chapelCount = 0;

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
    Set<TakenLecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user);
    List<TakenLecture> updatedTakenLectures = takenLectureDtoList
        .stream()
        .map(takenLectureDto -> new TakenLecture(
            user,
            getLectureToLectureCode(takenLectureDto.getLectureCode()),
            takenLectureDto.getYear(),
            takenLectureDto.getSemester()
        ))
        .filter(takenLecture -> !previousLectures.contains(takenLecture))
        .collect(Collectors.toList());
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
        .map(this::getEditedLecture)
        .collect(Collectors.toList());
    List<Lecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(TakenLecture::getLecture)
            .collect(Collectors.toList());
    Lecture chapel = lectureRepository.findByLectureCode(LectureCode.of("KMA02101")).orElseThrow(()->new IllegalArgumentException("데이터베이스 에러"));
    long addChapelCount = addedLectures
        .stream()
        .filter(addedLecture -> addedLecture.equals(chapel))
        .count();
    this.chapelCount = previousLectures
        .stream()
        .filter(previousLecture -> previousLecture.equals(chapel))
        .count();

    List<TakenLecture> addedTakenLectures = addedLectures
        .stream()
        .filter(addedLecture -> containLectures(addedLecture, previousLectures, chapel))
        .map(addedLecture -> convertCustomTakenLecture(user, addedLecture, chapel))
        .collect(Collectors.toList());

    takenLectureRepository.saveAll(addedTakenLectures);
  }

  private Lecture getEditedLecture(long lectureId) {
    return lectureRepository
        .findById(lectureId)
        .orElseThrow(() -> new IllegalArgumentException("수정하고자 하는 과목이 존재하지 않습니다."));
  }

  private Lecture getLectureToLectureCode(LectureCode lectureCode) {
    return lectureRepository
        .findByLectureCode(lectureCode)
        .orElseThrow(() -> new IllegalArgumentException("해당과목이 데이터에 존재하지 않습니다."));
  }

  private boolean containLectures(Lecture addTakenLecture, List<Lecture> previousLectures, Lecture chapel){
    Set<Lecture> previousLectureSet = new HashSet<>(previousLectures);
    if(addTakenLecture.equals(chapel) && this.chapelCount <4){
      this.chapelCount++;
      return true;
    }
    if(!previousLectureSet.contains(addTakenLecture)) {
      return true;
    }
    return false;
  }

  private TakenLecture convertCustomTakenLecture(User user, Lecture addLecture, Lecture chapel){
    if(addLecture.equals(chapel)){
      Random random = new Random();
      return new TakenLecture(user, addLecture, "커스텀", Integer.toString(random.nextInt()));
    }
    return new TakenLecture(user, addLecture,"커스텀", "커스텀");
  }


}
