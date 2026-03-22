package com.memo_v2.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@DisplayName("ActivityTracker Tests")
class ActivityTrackerTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    @DisplayName("Constructor should use default storage directory")
    void testDefaultConstructor() {
        ActivityTracker tracker = new ActivityTracker();
        assertEquals("./log", tracker.getStorageDirectory());
    }
    
    @Test
    @DisplayName("Parameterized constructor should set custom storage directory")
    void testParameterizedConstructor() {
        String customDir = "./custom_storage";
        ActivityTracker tracker = new ActivityTracker(customDir);
        assertEquals(customDir, tracker.getStorageDirectory());
    }
    
    @Test
    @DisplayName("setStorageDirectory should update the directory")
    void testSetStorageDirectory() {
        ActivityTracker tracker = new ActivityTracker();
        String newDir = "./new_storage";
        tracker.setStorageDirectory(newDir);
        assertEquals(newDir, tracker.getStorageDirectory());
    }
    
    @Test
    @DisplayName("getLastDistinctDescriptions should return descriptions from all files")
    void testGetLastDistinctDescriptions() throws Exception {
        String filePath1 = tempDir.resolve("project_tracking_20240315.csv").toString();
        String filePath2 = tempDir.resolve("project_tracking_20240316.csv").toString();
        
        // Create first file with entries
        Files.writeString(Path.of(filePath1), "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "15/03/2024 10:00;DEV;Task A;DONE;;0.5\n" +
            "15/03/2024 11:00;TEST;Task B;DONE;;0.25");
        
        // Create second file with entries
        Files.writeString(Path.of(filePath2), "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "16/03/2024 10:00;DEV;Task C;DONE;;0.5\n" +
            "16/03/2024 11:00;TEST;Task A;DONE;;0.25");
        
        ActivityTracker tracker = new ActivityTracker(tempDir.toString());
        List<String> descriptions = tracker.getLastDistinctDescriptions(5);
        
        assertEquals(3, descriptions.size());
        assertEquals("Task A", descriptions.get(0)); // Most recent (last entry of newest file)
        assertEquals("Task C", descriptions.get(1)); // First entry of newest file
        assertEquals("Task B", descriptions.get(2)); // Last entry of oldest file
    }
    
    @Test
    @DisplayName("getLastDistinctDescriptions should respect limit")
    void testGetLastDistinctDescriptionsLimit() throws Exception {
        String filePath = tempDir.resolve("project_tracking_20240315.csv").toString();
        Files.writeString(Path.of(filePath), "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "15/03/2024 10:00;DEV;Task A;DONE;;0.5\n" +
            "15/03/2024 11:00;TEST;Task B;DONE;;0.25\n" +
            "15/03/2024 12:00;DEV;Task C;DONE;;0.5");
        
        ActivityTracker tracker = new ActivityTracker(tempDir.toString());
        List<String> descriptions = tracker.getLastDistinctDescriptions(2);
        
        assertEquals(2, descriptions.size());
    }
    
    @Test
    @DisplayName("getLastDistinctDescriptions should skip empty descriptions")
    void testGetLastDistinctDescriptionsSkipEmpty() throws Exception {
        String filePath = tempDir.resolve("project_tracking_20240315.csv").toString();
        Files.writeString(Path.of(filePath), "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "15/03/2024 10:00;DEV;;DONE;;0.5\n" +
            "15/03/2024 11:00;TEST;Task A;DONE;;0.25");
        
        ActivityTracker tracker = new ActivityTracker(tempDir.toString());
        List<String> descriptions = tracker.getLastDistinctDescriptions(5);
        
        assertEquals(1, descriptions.size());
        assertEquals("Task A", descriptions.get(0));
    }
    
    @Test
    @DisplayName("getLastDistinctDescriptions should handle non-existent directory")
    void testGetLastDistinctDescriptionsNonExistent() {
        ActivityTracker tracker = new ActivityTracker(tempDir.resolve("nonexistent").toString());
        List<String> descriptions = tracker.getLastDistinctDescriptions(5);
        
        assertTrue(descriptions.isEmpty());
    }
    
    @Test
    @DisplayName("getLastDistinctDescriptions should handle unreadable files")
    void testGetLastDistinctDescriptionsCorruptedFile() throws Exception {
        String validPath = tempDir.resolve("project_tracking_20240315.csv").toString();
        String invalidPath = tempDir.resolve("corrupted_tracking_20240316.csv").toString();
        
        Files.writeString(Path.of(validPath), "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "15/03/2024 10:00;DEV;Valid Task;DONE;;0.5");
        
        Files.writeString(Path.of(invalidPath), "corrupted content not valid csv");
        
        ActivityTracker tracker = new ActivityTracker(tempDir.toString());
        List<String> descriptions = tracker.getLastDistinctDescriptions(5);
        
        assertEquals(1, descriptions.size());
        assertEquals("Valid Task", descriptions.get(0));
    }
}
