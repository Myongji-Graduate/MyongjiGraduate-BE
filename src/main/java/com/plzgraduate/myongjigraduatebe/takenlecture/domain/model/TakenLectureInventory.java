package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

public class TakenLectureInventory implements  Serializable {
	private static final long serialVersionUID = 1L;

	private final Set<TakenLecture> takenLecture;

	@Builder
	private TakenLectureInventory(Set<TakenLecture> takenLecture) {
		this.takenLecture = new HashSet<>(takenLecture);
	}

	public static TakenLectureInventory from(Set<TakenLecture> takenLectures) {
		return TakenLectureInventory.builder()
			.takenLecture(takenLectures)
			.build();
	}

	public Set<TakenLecture> getTakenLectures() {
		return Collections.unmodifiableSet(takenLecture);
	}

	public TakenLectureInventory copy() {
		return TakenLectureInventory.from(new HashSet<>(takenLecture));
	}

	public Set<TakenLecture> getCultureLectures() {
		return takenLecture.stream()
			.filter(taken -> taken.getLecture()
				.isCulture())
			.collect(Collectors.toSet());
	}

	public void handleFinishedTakenLectures(Set<TakenLecture> finishedTakenLecture) {
		this.takenLecture.removeAll(finishedTakenLecture);
	}

	public void sync(Set<Lecture> finishedLectures) {
		takenLecture.removeAll(
			takenLecture.stream()
				.filter(taken -> finishedLectures.contains(taken.getLecture()))
				.collect(Collectors.toSet())
		);
	}

	public int calculateTotalCredit() {
		int totalCredit = this.takenLecture
			.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture()
				.getCredit())
			.sum();
		if (checkChapelCountIsFour(this.takenLecture)) {
			totalCredit += 2;
		}
		return totalCredit;
	}

	private boolean checkChapelCountIsFour(Set<TakenLecture> takenLectures) {
		long chapelCount = takenLectures
			.stream()
			.filter(takenLecture -> takenLecture.getLecture()
				.getId()
				.equals("KMA02101"))
			.count();
		return chapelCount >= 4;
	}

	@Override
	public String toString() {
		return "TakenLectureInventory{" +
			"takenLecture=" + takenLecture +
			'}';
	}

	private static final Set<String> CHRISTIAN_COURSE_CODES = Set.of(
			"KMA00101", // 성서와인간이해
			"KMA02102", // 현대사회와기독교윤리
			"KMA02103", // 종교와과학
			"KMA02122"  // 기독교와문화
	);

	public Set<Lecture> getChristianLectures() {
		return takenLecture.stream()
				.map(TakenLecture::getLecture)
				.filter(lecture -> CHRISTIAN_COURSE_CODES.contains(lecture.getId()))
				.collect(Collectors.toSet());
	}

	public double calculateChristianCredits() {
		return getChristianLectures().stream()
				.mapToDouble(Lecture::getCredit)
				.sum();
	}

}
