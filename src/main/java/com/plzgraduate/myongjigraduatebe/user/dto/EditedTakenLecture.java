package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class EditedTakenLecture {

  private List<Long> deletedTakenLectures;
  private List<Long> addedTakenLectures;

}
