package com.memo.v2.service;

import com.memo.v2.model.ActivityEntry;
import com.memo.v2.model.ActivityType;
import com.memo.v2.model.Status;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service class for managing activity entries.
 * Handles CSV file operations, entry CRUD, and time calculations.
 */
public class ActivityService {

    private static final String FILE_PATTERN = "%s_tracking_%d.csv";
    private static final String DEFAULT_STORAGE_DIR = "./log";
    private static final double HOURS_PER_PROPORTIONAL_DAY = 7.75;
    
    private String projectId;
    private String storageDirectory;

    public ActivityService() {
        this.projectId = "MEMO";
        this.storageDirectory = DEFAULT_STORAGE_DIR;
    }

    public ActivityService(String projectId, String storageDirectory) {
        this.projectId = projectId;
        this.storageDirectory = storageDirectory;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    /**
     * Get the file path for a given date.
     * Format: {project}_tracking_{yyyyMMdd}.csv
     */
    public Path getFilePathForDate(LocalDate date) {
        int yearMonthDay = (date.getYear() * 10000) + (date.getMonthValue() * 100) + date.getDayOfMonth();
        return Paths.get(storageDirectory, String.format(FILE_PATTERN, projectId, yearMonthDay));
    }

    /**
     * Get the file path for a given LocalDateTime.
     */
    public Path getFilePathForTimestamp(LocalDateTime timestamp) {
        return getFilePathForDate(timestamp.toLocalDate());
    }

    /**
     * Ensure storage directory exists.
     */
    public void ensureStorageDirectoryExists() throws IOException {
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    /**
     * List all CSV files for the current project, sorted by date descending.
     */
    public List<Path> listAllFiles() throws IOException {
        ensureStorageDirectoryExists();
        
        Path storagePath = Paths.get(storageDirectory);
        List<Path> files = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storagePath)) {
            for (Path entry : stream) {
                if (entry.toString().endsWith("_tracking_*.csv")) {
                    files.add(entry);
                }
            }
        }
        
        // Sort by date in filename (descending)
        files.sort((a, b) -> {
            String nameA = a.getFileName().toString();
            String nameB = b.getFileName().toString();
            int dateA = extractDateFromFilename(nameA);
            int dateB = extractDateFromFilename(nameB);
            return Integer.compare(dateB, dateA); // Descending order
        });
        
        return files;
    }

    /**
     * Extract date integer from filename (e.g., "MEMO_tracking_20240101.csv" -> 20240101)
     */
    private int extractDateFromFilename(String filename) {
        try {
            int underscoreIndex = filename.indexOf("_tracking_");
            if (underscoreIndex != -1) {
                int dotIndex = filename.indexOf(".csv");
                String datePart = filename.substring(underscoreIndex + 10, dotIndex);
                return Integer.parseInt(datePart);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    /**
     * Parse CSV file and return list of ActivityEntry objects.
     */
    public List<ActivityEntry> readEntries(Path filePath) throws IOException {
        List<ActivityEntry> entries = new ArrayList<>();
        
        if (!Files.exists(filePath)) {
            return entries;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath)));
             CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withSeparator(';').withIgnoreSurroundingQuotes(true)) {
            
            for (CSVRecord record : parser) {
                if (record.isHeader()) continue;
                
                try {
                    ActivityEntry entry = new ActivityEntry();
                    entry.setProjectId(projectId);
                    
                    // Parse activity type
                    entry.setActivityType(ActivityType.fromString(record.get(0)));
                    
                    // Parse description
                    entry.setDescription(record.get(1));
                    
                    // Parse status
                    entry.setStatus(Status.fromString(record.get(2)));
                    
                    // Parse comment
                    entry.setComment(record.get(3) != null ? record.get(3).trim() : "");
                    
                    // Parse timestamp
                    if (record.get(4) != null && !record.get(4).isEmpty()) {
                        entry.setTimestamp(LocalDateTime.parse(record.get(4), formatter));
                    }
                    
                    // Parse time spent
                    if (record.get(5) != null && !record.get(5).isEmpty()) {
                        try {
                            entry.setTimeSpent(Double.parseDouble(record.get(5)));
                        } catch (NumberFormatException e) {
                            entry.setTimeSpent(0.0);
                        }
                    } else {
                        entry.setTimeSpent(0.0);
                    }
                    
                    entries.add(entry);
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
        }
        
        return entries;
    }

    /**
     * Write activity entries to CSV file.
     * Creates the file if it doesn't exist, appends to existing file.
     */
    public void writeEntry(Path filePath, ActivityEntry entry) throws IOException {
        ensureStorageDirectoryExists();
        
        boolean fileExists = Files.exists(filePath);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withSeparator(';').withIgnoreSurroundingQuotes(true))) {
            
            if (!fileExists) {
                // Write header
                printer.print("ACTIVITY_TYPE");
                printer.print(";");
                printer.print("DESCRIPTION");
                printer.print(";");
                printer.print("STATUS");
                printer.print(";");
                printer.print("COMMENT");
                printer.print(";");
                printer.print("TIMESTAMP");
                printer.print(";");
                printer.print("TIME_SPENT");
                printer.println();
            }
            
            // Write entry
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            printer.print(entry.getActivityType().name());
            printer.print(";");
            printer.print(entry.getDescription());
            printer.print(";");
            printer.print(entry.getStatus().name());
            printer.print(";");
            printer.print(entry.getComment() != null ? entry.getComment() : "");
            printer.print(";");
            printer.print(entry.getTimestamp().format(formatter));
            printer.print(";");
            printer.print(entry.getTimeSpent() != null ? String.format("%.3f", entry.getTimeSpent()) : "0.000");
            printer.println();
        }
    }

    /**
     * Add a new activity entry.
     */
    public void addEntry(ActivityEntry entry) throws IOException {
        Path filePath = getFilePathForTimestamp(entry.getTimestamp());
        writeEntry(filePath, entry);
    }

    /**
     * Calculate daily time summary for a given date.
     */
    public DailyTimeSummary calculateDailySummary(LocalDate date) throws IOException {
        Path filePath = getFilePathForDate(date);
        List<ActivityEntry> entries = readEntries(filePath);
        
        DailyTimeSummary summary = new DailyTimeSummary();
        summary.setDate(date);
        summary.setFilePath(filePath);
        
        Map<String, Double> byDescription = new HashMap<>();
        Map<String, Double> byType = new HashMap<>();
        
        for (ActivityEntry entry : entries) {
            Double timeSpent = entry.getTimeSpent();
            if (timeSpent == null) timeSpent = 0.0;
            
            // Group by description
            String desc = entry.getDescription();
            byDescription.put(desc, byDescription.getOrDefault(desc, 0.0) + timeSpent);
            
            // Group by activity type
            if (entry.getActivityType() != null) {
                String type = entry.getActivityType().getDisplayName();
                byType.put(type, byType.getOrDefault(type, 0.0) + timeSpent);
            }
            
            summary.addEntry(entry);
            summary.totalTimeSpent += timeSpent;
        }
        
        summary.setByDescription(byDescription);
        summary.setByType(byType);
        summary.totalTimeInHours = summary.totalTimeSpent * HOURS_PER_PROPORTIONAL_DAY;
        
        return summary;
    }

    /**
     * Calculate weekly time summary.
     */
    public WeeklyTimeSummary calculateWeeklySummary(LocalDate startDate, LocalDate endDate) throws IOException {
        WeeklyTimeSummary summary = new WeeklyTimeSummary();
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);
        
        Map<String, Double> byDescription = new HashMap<>();
        Map<String, Double> byType = new HashMap<>();
        Map<LocalDate, Double> byDay = new HashMap<>();
        
        for (LocalDate date : Dates.between(startDate, endDate)) {
            DailyTimeSummary dailySummary = calculateDailySummary(date);
            summary.addDailySummary(dailySummary);
            
            // Aggregate by description
            for (Map.Entry<String, Double> entry : dailySummary.getByDescription().entrySet()) {
                byDescription.put(entry.getKey(), byDescription.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
            
            // Aggregate by type
            for (Map.Entry<String, Double> entry : dailySummary.getByType().entrySet()) {
                byType.put(entry.getKey(), byType.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
            
            // Track daily total
            byDay.put(date, dailySummary.getTotalTimeSpent());
        }
        
        summary.setByDescription(byDescription);
        summary.setByType(byType);
        summary.setByDay(byDay);
        
        return summary;
    }

    // Inner classes for summaries
    public static class DailyTimeSummary {
        private LocalDate date;
        private Path filePath;
        private List<ActivityEntry> entries = new ArrayList<>();
        private Map<String, Double> byDescription = new HashMap<>();
        private Map<String, Double> byType = new HashMap<>();
        private double totalTimeSpent;
        private double totalTimeInHours;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Path getFilePath() { return filePath; }
        public void setFilePath(Path filePath) { this.filePath = filePath; }
        
        public List<ActivityEntry> getEntries() { return entries; }
        public void addEntry(ActivityEntry entry) { entries.add(entry); }
        
        public Map<String, Double> getByDescription() { return byDescription; }
        public void setByDescription(Map<String, Double> byDescription) { this.byDescription = byDescription; }
        
        public Map<String, Double> getByType() { return byType; }
        public void setByType(Map<String, Double> byType) { this.byType = byType; }
        
        public double getTotalTimeSpent() { return totalTimeSpent; }
        public double getTotalTimeInHours() { return totalTimeInHours; }
    }

    public static class WeeklyTimeSummary {
        private LocalDate startDate;
        private LocalDate endDate;
        private List<DailyTimeSummary> dailySummaries = new ArrayList<>();
        private Map<String, Double> byDescription = new HashMap<>();
        private Map<String, Double> byType = new HashMap<>();
        private Map<LocalDate, Double> byDay = new HashMap<>();

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public List<DailyTimeSummary> getDailySummaries() { return dailySummaries; }
        public void addDailySummary(DailyTimeSummary summary) { dailySummaries.add(summary); }
        
        public Map<String, Double> getByDescription() { return byDescription; }
        public void setByDescription(Map<String, Double> byDescription) { this.byDescription = byDescription; }
        
        public Map<String, Double> getByType() { return byType; }
        public void setByType(Map<String, Double> byType) { this.byType = byType; }
        
        public Map<LocalDate, Double> getByDay() { return byDay; }
        public void setByDay(Map<LocalDate, Double> byDay) { this.byDay = byDay; }
    }

    // Helper class for date range iteration
    private static class Dates {
        public static Iterable<LocalDate> between(LocalDate start, LocalDate end) {
            List<LocalDate> dates = new ArrayList<>();
            LocalDate current = start;
            while (!current.isAfter(end)) {
                dates.add(current);
                current = current.plusDays(1);
            }
            return () -> dates.iterator();
        }
    }
}
