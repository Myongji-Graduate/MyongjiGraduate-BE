package com.plzgraduate.myongjigraduatebe.user.service;

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
import com.plzgraduate.myongjigraduatebe.user.dto.SavedTakenLectureDto;
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

    List<SavedTakenLectureDto> savedTakenLectureDtoList = parsingTextDto.getTakenLectureDto();
    Set<TakenLecture> previousLectures =
        takenLectureRepository
            .findAllByUserWithFetchJoin(user);
    List<TakenLecture> updatedTakenLectures = savedTakenLectureDtoList
        .stream().
        map(savedTakenLectureDto -> new TakenLecture(
            user,
            getLectureToLectureCode(savedTakenLectureDto.getLectureCode()),
            savedTakenLectureDto.getYear(),
            savedTakenLectureDto.getSemester()
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

    deleteTakenLecture(editUser, editedTakenLecture.getDeletedTakenLectures());
    addTakenLecture(editUser, editedTakenLecture.getAddedTakenLectures());
  }

  @Override
  public List<TakenLecture> findAllByUserId(long id) {
    return takenLectureRepository.findAllByUserIdWithFetchJoin(id);
  }

  public void deleteTakenLecture(
      User user,
      List<Long> deletedTakenLectures
  ) {
    List<Lecture> deleteTakenLectures = deletedTakenLectures
        .stream()
        .map(this::getEditedLecture)
        .collect(Collectors.toList());
    takenLectureRepository.deleteAllByUserAndLectureIsIn(user, deleteTakenLectures);
  }

  public void addTakenLecture(
      User user,
      List<Long> addedTakenLecture
  ) {
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
    List<TakenLecture> addedTakenLectures = addedLectures
        .stream()
        .filter(addedLecture -> !previousLectures.contains(addedLecture))
        .collect(Collectors.toList())
        .stream()
        .map(addedLecture -> new TakenLecture(user, addedLecture, "커스텀", "커스텀"))
        .collect(Collectors.toList());
    takenLectureRepository.saveAll(addedTakenLectures);
  }

  private Lecture getEditedLecture(long lectureId) {
    return lectureRepository
        .findById(lectureId)
        .orElseThrow(() -> new IllegalArgumentException("수정하고자 하는 과목이 존재하지 않습니다."));
  }

  private Lecture getLectureToLectureCode(LectureCode lectureCode){
    return lectureRepository.findByLectureCode(lectureCode).orElseThrow(()-> new IllegalArgumentException("해당과목이 데이터에 존재하지 않습니다."));
  }

}
