package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture_catalog_revision")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureCatalogRevisionJpaEntity {
    @Id
    private Integer id;
    private long revision;
}
