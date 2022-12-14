package com.plzgraduate.myongjigraduatebe.department.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

  @Column()
  private String name;

  public Department(String name) {
    this.name = name;
  }
}
