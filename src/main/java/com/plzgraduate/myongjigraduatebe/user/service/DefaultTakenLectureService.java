package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
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
      Long userId,
      String[] lectureText
  ) {
    User user = userRepository.findUserById(userId);
    List<LectureCode> takenLectureCodes = getTakenLectureCodes(lectureText);
    List<Lecture> lectures = lectureRepository.findAllByLectureCodeIsIn(takenLectureCodes);
    lectures.forEach(lecture -> takenLectureRepository.save(new TakenLecture(user, lecture)));
  }

  private List<LectureCode> getTakenLectureCodes(
      String[] splitText
  ) {
    int countTakenLecture = (splitText.length - 9) / 8;
    List<LectureCode> takenLectureCods = new ArrayList<>(countTakenLecture);
    for (int i = 9; i < splitText.length; i += 7) {
      String code = splitText[i + 3];
      if (i + 7 < splitText.length && !splitText[i + 7].isBlank() && Character.isDigit(splitText[i + 7].charAt(0))) {
        i++;
      }
      takenLectureCods.add(LectureCode.of(code));
    }
    return takenLectureCods;
  }

}
