package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;
import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.common.entity.EntryYearConverter;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Convert(converter = UserIdConverter.class)
  @Column(columnDefinition = "varchar(255)", unique = true, nullable = false)
  private UserId userId;

  @Column(columnDefinition = "char(60)", nullable = false)
  private String password;

  @Convert(converter = StudentNumberConverter.class)
  @Column(columnDefinition = "char(8)", unique = true, nullable = false)
  private StudentNumber studentNumber;

  @Convert(converter = EntryYearConverter.class)
  @Column(columnDefinition = "TINYINT UNSIGNED")
  private EntryYear entryYear;

  private String name;

  @Enumerated(value = EnumType.STRING)
  private EnglishLevel engLv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_DEPARTMENT_USER"))
  private Department department;

  @Enumerated(value = EnumType.STRING)
  private Role role = Role.USER;

  public List<String> getRoles() {
    return role.getRoleList();
  }

  public User(
      UserId userId,
      String password,
      StudentNumber studentNumber,
      EnglishLevel engLv
  ) {
    this.userId = userId;
    this.password = password;
    this.studentNumber = studentNumber;
    this.engLv = engLv;
  }

  public void updateRole(Role role) {
    this.role = role;
  }

  @RequiredArgsConstructor
  public enum Role {
    USER("USER"),
    MANAGER("MANAGER,USER"),
    ADMIN("ADMIN,MANAGER,USER");

    private final String roles;

    public List<String> getRoleList() {
      return Arrays.asList(roles.split(","));
    }
  }
}
