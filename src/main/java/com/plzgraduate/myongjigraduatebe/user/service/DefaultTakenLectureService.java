package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.user.dto.EditedTakenLecture;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.repository.TakenLectureRepository;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultTakenLectureService implements TakenLectureService {
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
      throw new IllegalArgumentException("본인의 PDF 파일을 올려주세요. ");
    }
    List<LectureCode> takenLectureCodes = parsingTextDto.getTakenLectureCods();
    List<Lecture> lectures = lectureRepository.findAllByLectureCodeIsIn(takenLectureCodes);
    List<Lecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(TakenLecture::getLecture)
            .collect(Collectors.toList());
    List<Lecture> updatedLectures = lectures
        .stream()
        .filter(lecture -> !previousLectures.contains(lecture))
        .collect(Collectors.toList());
    List<TakenLecture> updatedTakenLectures = updatedLectures
        .stream().
        map(updatedLecture -> new TakenLecture(user, updatedLecture))
        .collect(Collectors.toList());
    takenLectureRepository.saveAll(updatedTakenLectures);
  }

  @Override
  public TakenLectureResponse showTakenLecture(AuthenticatedUser authUser) {
    User user = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

    List<Lecture> lectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(takenLecture -> takenLecture.getLecture())
            .collect(Collectors.toList());
    List<TakenLectureDto> takenLectureDtoList = lectures
        .stream()
        .map(lecture -> TakenLectureDto.from(lecture))
        .collect(Collectors.toList());
    return TakenLectureResponse.of(takenLectureDtoList);
  }

  @Override
  public void editTakenLecture(
      AuthenticatedUser authUser,
      EditedTakenLecture editedTakenLecture
  ) {
    User editUser = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

    deleteTakenLecture(editUser, editedTakenLecture.getDeletedTakenLectures());
    addTakenLecture(editUser, editedTakenLecture.getAddedTakenLectures());
  }

  public void deleteTakenLecture(
      User user,
      List<Long> deletedTakenLectures
  ) {
    List<Lecture> deleteTakenLectures = deletedTakenLectures
        .stream()
        .map(lectureId -> getEditedLecture(lectureId))
        .collect(Collectors.toList());
    takenLectureRepository.deleteAllByUserAndLectureIsIn(user, deleteTakenLectures);
  }

  public void addTakenLecture(
      User user,
      List<Long> addedTakenLecture
  ) {
    List<Lecture> addedLectures = addedTakenLecture
        .stream()
        .map(lectureId -> getEditedLecture(lectureId))
        .collect(Collectors.toList());
    List<Lecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user)
            .stream()
            .map(takenLecture -> takenLecture.getLecture())
            .collect(Collectors.toList());
    List<TakenLecture> addedTakenLectures = addedLectures
        .stream()
        .filter(addedLecture -> !previousLectures.contains(addedLecture))
        .collect(Collectors.toList())
        .stream()
        .map(addedLecture -> new TakenLecture(user, addedLecture))
        .collect(Collectors.toList());
    takenLectureRepository.saveAll(addedTakenLectures);
  }

  private Lecture getEditedLecture(long lectureId) {
    return lectureRepository
        .findById(lectureId)
        .orElseThrow(() -> new IllegalArgumentException("수정하고자 하는 과목이 존재하지 않습니다."));
  }

}
