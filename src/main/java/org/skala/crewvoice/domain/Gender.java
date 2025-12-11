package org.skala.crewvoice.domain;

/**
 * 성별 Enum
 */
public enum Gender {
    M,  // 남성
    F;  // 여성

    public static Gender fromString(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }

        String upperGender = gender.toUpperCase();
        if (upperGender.equals("M") || upperGender.equals("MALE")) {
            return M;
        } else if (upperGender.equals("F") || upperGender.equals("FEMALE")) {
            return F;
        }

        throw new IllegalArgumentException("Invalid gender: " + gender);
    }
}
