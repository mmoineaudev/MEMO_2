package com.memo_v2.model;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVFile {
    private String filePath;
    private List<ActivityEntry> entries = new ArrayList<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.*)_tracking_(\\d{8})\\.csv");
    
    public CSVFile(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public List<ActivityEntry> getEntries() { return entries; }
    public void setEntries(List<ActivityEntry> entries) { this.entries = entries; }
    
    public String getProjectName() {
        if (filePath != null && !filePath.isEmpty()) {
            int lastSlash = filePath.lastIndexOf('/');
            String filename = lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
            Matcher matcher = FILENAME_PATTERN.matcher(filename);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
    }
    
    public LocalDate getDate() {
        if (filePath != null && !filePath.isEmpty()) {
            int lastSlash = filePath.lastIndexOf('/');
            String filename = lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
            Matcher matcher = FILENAME_PATTERN.matcher(filename);
            if (matcher.matches()) {
                try {
                    return LocalDate.parse(matcher.group(2), DATE_FORMATTER);
                } catch (Exception e) {
                    // Return null or handle error
                }
            }
        }
        return null;
    }
    
    public void loadFromFile() throws IOException {
        entries.clear();
        File file = new File(filePath);
        System.err.println("DEBUG CSVFile.loadFromFile: filePath=" + this.filePath + " file.exists=" + file.exists());
        if (!file.exists()) {
            createNewFile();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header
                }
                if (line.trim().isEmpty()) continue;
                
                ActivityEntry entry = parseCSVLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }
    }
    
    public void saveToFile() throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("timestamp;activity_type;description;status;comment;time_spent_days");
            writer.newLine();
            
            // Write entries
            for (ActivityEntry entry : entries) {
                String line = toCSVLine(entry);
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    public void createNewFile() throws IOException {
        File file = new File(filePath);
        System.err.println("DEBUG CSVFile: Creating file at " + file.getAbsolutePath());
        System.err.println("DEBUG CSVFile: filePath=" + this.filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("timestamp;activity_type;description;status;comment;time_spent_days");
            writer.newLine();
        }
    }
    
    private ActivityEntry parseCSVLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 6) return null;
        
        try {
            LocalDateTime timestamp = LocalDateTime.parse(parts[0], 
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            ActivityEntry entry = new ActivityEntry();
            entry.setTimestamp(timestamp);
            entry.setActivityType(parts[1]);
            entry.setDescription(parts[2]);
            entry.setStatus(parts[3]);
            // Unescape newlines in comment
            String rawComment = parts[4].replace("\\n", "\n");
            entry.setComment(rawComment);
            entry.setTimeSpentDays(Double.parseDouble(parts[5]));
            return entry;
        } catch (Exception e) {
            // Log error and skip
            return null;
        }
    }
    
    private String toCSVLine(ActivityEntry entry) {
        // Escape newlines in comment for CSV storage
        String escapedComment = entry.getComment().replace("\n", "\\n");
        return String.format("%s;%s;%s;%s;%s;%.6f",
            entry.getTimestampFormatted(),
            entry.getActivityType(),
            entry.getDescription(),
            entry.getStatus(),
            escapedComment,
            entry.getTimeSpentDays());
    }
}