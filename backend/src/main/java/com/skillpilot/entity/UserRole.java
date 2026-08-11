package com.skillpilot.entity;

public enum UserRole {
    GUEST("guest"),
    STUDENT("student"),
    ADMIN("admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String text) {
        for (UserRole r : UserRole.values()) {
            if (r.value.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return STUDENT;
    }
}
