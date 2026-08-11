package com.skillpilot.entity;

public enum DemandLevel {
    HIGH("High"),
    VERY_HIGH("Very High"),
    MODERATE("Moderate");

    private final String value;

    DemandLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DemandLevel fromValue(String text) {
        for (DemandLevel d : DemandLevel.values()) {
            if (d.value.equalsIgnoreCase(text)) {
                return d;
            }
        }
        return HIGH;
    }
}
