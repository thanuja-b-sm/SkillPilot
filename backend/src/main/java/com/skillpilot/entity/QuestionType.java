package com.skillpilot.entity;

public enum QuestionType {
    SINGLE("single"),
    MULTIPLE("multiple"),
    SCALE("scale");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QuestionType fromValue(String text) {
        for (QuestionType q : QuestionType.values()) {
            if (q.value.equalsIgnoreCase(text)) {
                return q;
            }
        }
        return SINGLE;
    }
}
