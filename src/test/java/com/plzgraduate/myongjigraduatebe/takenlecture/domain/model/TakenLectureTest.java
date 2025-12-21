package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TakenLectureTest {

    private static User user(String authId) {
        return User.builder()
                .id(1L)
                .authId(authId)
                .englishLevel(EnglishLevel.BASIC)
                .koreanLevel(KoreanLevel.KOR12)
                .studentNumber("20200001")
                .entryYear(20)
                .studentCategory(StudentCategory.NORMAL)
                .build();
    }

    private static Lecture lecture(String code, String name, int credit) {
        return Lecture.from(code, name, credit);
    }

    @Test
    @DisplayName("of(): 필드 세팅 및 toString() 호출")
    void of_setsFields() {
        User u = user("u1");
        Lecture l = lecture("KMA00001", "알고리즘", 3);

        TakenLecture t = TakenLecture.of(u, l, 2020, Semester.FIRST);

        assertThat(t.getUser()).isSameAs(u);
        assertThat(t.getLecture()).isSameAs(l);
        assertThat(t.getYear()).isEqualTo(2020);
        assertThat(t.getSemester()).isEqualTo(Semester.FIRST);
        assertThat(t.toString()).contains("TakenLecture{");
    }

    @Test
    @DisplayName("custom(): year=2099로 설정되고 semester는 null")
    void custom_setsYear2099() {
        TakenLecture t = TakenLecture.custom(user("u2"), lecture("KMB00001", "컴퓨터개론", 2));
        assertThat(t.getYear()).isEqualTo(2099);
        assertThat(t.getSemester()).isNull();
    }

    @Test
    @DisplayName("takenAfter(): 경계 포함 비교 (>, =, <)")
    void takenAfter_boundaries() {
        TakenLecture t = TakenLecture.of(user("u3"), lecture("KMC00001", "자료구조", 3), 2020, Semester.SECOND);
        assertThat(t.takenAfter(2019)).isTrue();
        assertThat(t.takenAfter(2020)).isTrue();
        assertThat(t.takenAfter(2021)).isFalse();
    }

    @Test
    @DisplayName("equals/hashCode: 동일 필드면 동등, 다르면 비동등")
    void equals_hashCode() {
        User u = user("u4");
        Lecture l1 = lecture("KMD00001", "운영체제", 3);
        Lecture l2 = lecture("KMD00002", "컴퓨터구조", 2);

        TakenLecture a = TakenLecture.of(u, l1, 2020, Semester.FIRST);
        TakenLecture b = TakenLecture.of(u, l1, 2020, Semester.FIRST);
        TakenLecture c = TakenLecture.of(u, l2, 2020, Semester.FIRST);

        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b)
                .isNotEqualTo(c)
                .isNotEqualTo(null)
                .isNotEqualTo("str");
    }
}
