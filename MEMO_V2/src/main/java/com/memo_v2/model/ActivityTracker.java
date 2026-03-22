package com.memo_v2.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ActivityTracker {
    private String storageDirectory = "./log";
    
    public ActivityTracker() {}
    
    public ActivityTracker(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }
    
    public void setStorageDirectory(String directory) {
        this.storageDirectory = directory;
    }
    
    public String getStorageDirectory() {
        return storageDirectory;
    }
    
    /**
     * Get last 10 distinct descriptions from all CSV files
     */
    public List<String> getLastDistinctDescriptions(int limit) {
        Set<String> seen = new LinkedHashSet<>();
        List<File> csvFiles = findCSVFiles();
        
        // Sort by filename date descending (newest first)
        csvFiles.sort((f1, f2) -> {
            String name1 = f1.getName();
            String name2 = f2.getName();
            int date1 = Integer.parseInt(name1.substring(name1.lastIndexOf('_') + 1, name1.length() - 4));
            int date2 = Integer.parseInt(name2.substring(name2.lastIndexOf('_') + 1, name2.length() - 4));
            return Integer.compare(date2, date1);
        });
        
        List<String> descriptions = new ArrayList<>();
        for (File file : csvFiles) {
            try {
                CSVFile csvFile = new CSVFile(file.getAbsolutePath());
                csvFile.loadFromFile();
                
                // Iterate in reverse order to get most recent entries first
                List<ActivityEntry> entries = csvFile.getEntries();
                for (int i = entries.size() - 1; i >= 0; i--) {
                    String desc = entries.get(i).getDescription();
                    if (!desc.isEmpty() && !seen.contains(desc)) {
                        seen.add(desc);
                        descriptions.add(desc);
                        if (descriptions.size() >= limit) {
                            return descriptions;
                        }
                    }
                }
            } catch (Exception e) {
                // Skip files that can't be read
            }
        }
        
        return descriptions;
    }
    
    /**
     * Find all CSV tracking files in the storage directory
     */
    private List<File> findCSVFiles() {
        List<File> files = new ArrayList<>();
        File dir = new File(storageDirectory);
        if (!dir.exists()) return files;
        
        File[] matchingFiles = dir.listFiles((d, name) -> name.matches(".*_tracking_\\d{8}\\.csv"));
        if (matchingFiles != null) {
            files.addAll(Arrays.asList(matchingFiles));
        }
        return files;
    }
}