package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureJpaEntity {

    @Id
    private String id;

    private String name;

    private int credit;

    private String duplicateCode;

    private int isRevoked;

    @Builder
    private LectureJpaEntity(String id, String name, int credit,
            String duplicateCode, int isRevoked) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.duplicateCode = duplicateCode;
        this.isRevoked = isRevoked;
    }
}
