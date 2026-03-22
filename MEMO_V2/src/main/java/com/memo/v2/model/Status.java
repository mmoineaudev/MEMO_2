package com.memo.v2.model;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Status enumeration.
 * Represents the different statuses an activity can have.
 */
public enum Status {
    TODO("To Do"),
    DOING("In Progress"),
    DONE("Done"),
    NOTE("Note");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Status fromString(String value) {
        try {
            return value != null ? Status.valueOf(value.trim().toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static EnumSet<Status> allValues() {
        return EnumSet.allOf(Status.class);
    }

    public static java.util.List<Status> toList() {
        return Arrays.asList(values());
    }
}
