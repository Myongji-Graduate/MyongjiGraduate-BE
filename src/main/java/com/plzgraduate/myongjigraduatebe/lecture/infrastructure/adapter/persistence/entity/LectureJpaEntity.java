package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
