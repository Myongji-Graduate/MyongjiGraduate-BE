package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  private void validateUser(long id) {
    if (userRepository
        .findUserById(id)
        .isEmpty()) {
      throw new IllegalArgumentException("해당 유저가 없습니다");
    }
  }
}
