package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferCredit {

    private int normalCulture;
    private int majorLecture;
    private int freeElective;
    private int christianLecture;

    public static TransferCredit empty() {
        return new TransferCredit(0, 0, 0, 0);
    }

    public static TransferCredit from(String input) {
        if (isValid(input)) {
            String[] parts = input.split("/");
            int normalCulture = Integer.parseInt(parts[0]);
            int majorLecture = Integer.parseInt(parts[1]);
            int freeElective = Integer.parseInt(parts[2]);
            int christianLecture = Integer.parseInt(parts[3]);

            return new TransferCredit(normalCulture, majorLecture, freeElective, christianLecture);
        } else {
            throw new IllegalArgumentException("잘못된 형식입니다.");
        }
    }

    private static boolean isValid(String input) {
        return input.matches("^\\d+/\\d+/\\d+/\\d+$");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransferCredit that = (TransferCredit) o;
        return normalCulture == that.normalCulture && majorLecture == that.majorLecture
                && freeElective == that.freeElective && christianLecture == that.christianLecture;
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalCulture, majorLecture, freeElective, christianLecture);
    }

    @Override
    public String toString() {
        return normalCulture + "/"
                + majorLecture + "/"
                + freeElective + "/"
                + christianLecture;
    }
}