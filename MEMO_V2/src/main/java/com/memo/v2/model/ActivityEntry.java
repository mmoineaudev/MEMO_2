package com.memo.v2.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Activity entry data class.
 * Represents a single tracked activity with all its properties.
 */
public class ActivityEntry {

    private String projectId;
    private ActivityType activityType;
    private String description;
    private Status status;
    private String comment;
    private LocalDateTime timestamp;
    private Double timeSpent; // in proportional days (1 day = 7.75 hours)

    public ActivityEntry() {
    }

    public ActivityEntry(String projectId, ActivityType activityType, String description,
                         Status status, String comment, LocalDateTime timestamp, Double timeSpent) {
        this.projectId = projectId;
        this.activityType = activityType;
        this.description = description;
        this.status = status;
        this.comment = comment;
        this.timestamp = timestamp;
        this.timeSpent = timeSpent;
    }

    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Double timeSpent) { this.timeSpent = timeSpent; }

    // Convert time spent from days to hours
    public double getTimeSpentInHours() {
        return timeSpent != null ? timeSpent * 7.75 : 0;
    }

    // Convert time spent from days to minutes
    public int getTimeSpentInMinutes() {
        return timeSpent != null ? (int) (timeSpent * 7.75 * 60) : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ActivityEntry) {
            ActivityEntry that = (ActivityEntry) o;
            return Objects.equals(projectId, that.projectId) &&
                   Objects.equals(activityType, that.activityType) &&
                   Objects.equals(description, that.description) &&
                   Objects.equals(status, that.status) &&
                   Objects.equals(comment, that.comment) &&
                   Objects.equals(timestamp, that.timestamp) &&
                   Objects.equals(timeSpent, that.timeSpent);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, activityType, description, status, comment, timestamp, timeSpent);
    }

    @Override
    public String toString() {
        return "ActivityEntry{" +
               "projectId='" + projectId + '\'' +
               ", activityType=" + activityType +
               ", description='" + description + '\'' +
               ", status=" + status +
               ", comment='" + comment + '\'' +
               ", timestamp=" + timestamp +
               ", timeSpent=" + timeSpent +
               '}';
    }
}
