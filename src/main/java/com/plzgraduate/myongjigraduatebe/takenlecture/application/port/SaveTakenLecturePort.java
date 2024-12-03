package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import java.util.List;

public interface SaveTakenLecturePort {

	void saveTakenLectures(List<TakenLecture> takenLectures);

	void saveTakenLecture(TakenLecture takenLecture);
}
