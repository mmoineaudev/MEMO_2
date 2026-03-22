package com.memo.v2.model;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Activity type enumeration.
 * Represents the different types of activities that can be tracked.
 */
public enum ActivityType {
    DEV("Development"),
    TEST("Testing"),
    CEREMONY("Ceremony"),
    LEARNING("Learning"),
    CONTINUOUS_IMPROVEMENT("Continuous Improvement"),
    SUPPORT("Support"),
    ADMIN("Administration"),
    DOCUMENTATION("Documentation");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ActivityType fromString(String value) {
        try {
            return value != null ? ActivityType.valueOf(value.trim().toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static EnumSet<ActivityType> allValues() {
        return EnumSet.allOf(ActivityType.class);
    }

    public static java.util.List<ActivityType> toList() {
        return Arrays.asList(values());
    }
}
