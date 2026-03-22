package com.memo_v2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityEntry {
    private LocalDateTime timestamp;
    private String activityType;
    private String description;
    private String status;
    private String comment;
    private double timeSpentDays; // Proportional days (1 day = 7.75 hours)
    
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // Activity types
    public static final String DEV = "DEV";
    public static final String TEST = "TEST";
    public static final String CEREMONY = "CEREMONY";
    public static final String LEARNING = "LEARNING";
    public static final String CONTINUOUS_IMPROVEMENT = "CONTINUOUS_IMPROVEMENT";
    public static final String SUPPORT = "SUPPORT";
    public static final String ADMIN = "ADMIN";
    public static final String DOCUMENTATION = "DOCUMENTATION";
    
    // Status values
    public static final String TODO = "TODO";
    public static final String DOING = "DOING";
    public static final String DONE = "DONE";
    public static final String NOTE = "NOTE";
    
    public ActivityEntry() {}
    
    public ActivityEntry(LocalDateTime timestamp, String activityType, String description,
                         String status, String comment, double timeSpentDays) {
        this.timestamp = timestamp;
        this.activityType = activityType;
        this.description = description;
        this.status = status;
        this.comment = comment;
        this.timeSpentDays = timeSpentDays;
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public double getTimeSpentDays() { return timeSpentDays; }
    public void setTimeSpentDays(double timeSpentDays) { this.timeSpentDays = timeSpentDays; }
    
    public String getTimestampFormatted() {
        return timestamp != null ? timestamp.format(TIMESTAMP_FORMATTER) : "";
    }
    
    public String getDateOnlyFormatted() {
        return timestamp != null ? timestamp.format(DATE_ONLY_FORMATTER) : "";
    }
    
    // Convert proportional days to hours (1 day = 7.75 hours)
    public double getTimeSpentHours() {
        return timeSpentDays * 7.75;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s) [%s] %.3f days", 
            getTimestampFormatted(), activityType, description, status, comment, timeSpentDays);
    }
}
