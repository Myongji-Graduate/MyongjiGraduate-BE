package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
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
      Long id,
      String[] lectureText
  ) {
    User user = userRepository.findUserById(id);
    List<LectureCode> takenLectureCodes = getTakenLectureCodes(lectureText);
    List<Lecture> lectures = lectureRepository.findAllByLectureCodeIsIn(takenLectureCodes);
    lectures.forEach(lecture -> takenLectureRepository.save(new TakenLecture(user, lecture)));
  }

  @Override
  public TakenLectureResponse showTakenLecture(Long id) {
    User user = userRepository.findUserById(id);
    List<Lecture> lectures =
        takenLectureRepository.findAllByUserWithFetchJoin(user)
        .stream()
        .map(takenLecture -> takenLecture.getLecture())
        .collect(Collectors.toList());
    List<TakenLectureDto> takenLectureDtoList = new ArrayList<>();
    lectures
        .forEach(lecture -> takenLectureDtoList.add(TakenLectureDto
                                                        .builder()
                                                        .code(lecture.getCode())
                                                        .name(lecture.getName())
                                                        .credit(lecture.getCredit())
                                                        .build()));
    return new TakenLectureResponse(takenLectureDtoList);
  }

  private List<LectureCode> getTakenLectureCodes(
      String[] splitText
  ) {
    List<LectureCode> takenLectureCods = new ArrayList<>();
    for (int i = 9; i < splitText.length; i += 7) {
      if(Character.isDigit(splitText[i+3].charAt(0))){
        String code = splitText[i + 3];
        return takenLectureCods;
      }
      String code = splitText[i + 3];
      System.out.println(code);
      if (i + 7 < splitText.length && !splitText[i + 7].isBlank() && Character.isDigit(splitText[i + 7].charAt(0))) {
        i++;
      }
      takenLectureCods.add(LectureCode.of(code));
    }
    return takenLectureCods;
  }

}
