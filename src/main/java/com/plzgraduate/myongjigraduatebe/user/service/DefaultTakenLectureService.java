package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
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
      long id,
      ParsingTextDto parsingTextDto
  ) {

    validateUser(id);
    User user = userRepository
        .findUserById(id)
        .get();

    if (!(user
        .getStudentNumber()
        .equals(parsingTextDto.getStudentNumber()))) {
      throw new IllegalArgumentException("해당 학번이 이미 존재합니다");
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
  public TakenLectureResponse showTakenLecture(long id) {
    validateUser(id);
    User user = userRepository
        .findUserById(id)
        .get();
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
      long id,
      List<Long> deletedTakenLectures,
      List<Long> addedTakenLectures
  ) {
    validateUser(id);
    User editUser = userRepository
        .findUserById(id)
        .get();
    deleteTakenLecture(editUser, deletedTakenLectures);
    addTakenLecture(editUser, addedTakenLectures);
  }

  public void deleteTakenLecture(
      User user,
      List<Long> deletedTakenLectures
  ) {
    List<Lecture> deleteTakenLectures = deletedTakenLectures
        .stream()
        .map(lectureId -> getEditedLecture(lectureId))
        .collect(Collectors.toList());
    deleteTakenLectures.forEach(lecture -> takenLectureRepository.deleteTakenLectureByUserAndLecture(user, lecture));
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
    addedLectures
        .stream()
        .filter(addedLecture -> !previousLectures.contains(addedLecture))
        .forEach(filteredLecture -> takenLectureRepository.save(new TakenLecture(user, filteredLecture)));
  }

  private void validateUser(long id) {
    if (userRepository
        .findUserById(id)
        .isEmpty()) {
      throw new IllegalArgumentException("해당 유저가 없습니다");
    }
  }

  private Lecture getEditedLecture(long lectureId) {
    if (lectureRepository
        .findById(lectureId)
        .isEmpty()) {
      throw new IllegalArgumentException("추가하고자 하는 과목이 존재하지 않습니다");
    }
    return lectureRepository
        .findById(lectureId)
        .get();
  }
}
