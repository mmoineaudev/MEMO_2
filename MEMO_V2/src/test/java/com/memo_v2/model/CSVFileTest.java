package com.memo_v2.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;

@DisplayName("CSVFile Tests")
class CSVFileTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    @DisplayName("getProjectName should extract project name from file path")
    void testGetProjectName() throws Exception {
        String filePath = tempDir.resolve("myproject_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        assertEquals("myproject", csvFile.getProjectName());
    }
    
    @Test
    @DisplayName("getDate should extract date from filename")
    void testGetDate() throws Exception {
        String filePath = tempDir.resolve("test_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        assertEquals(java.time.LocalDate.of(2024, 3, 15), csvFile.getDate());
    }
    
    @Test
    @DisplayName("createNewFile should create file with header")
    void testCreateNewFile() throws Exception {
        String filePath = tempDir.resolve("new_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        csvFile.createNewFile();
        
        assertTrue(Files.exists(Path.of(filePath)));
        String content = Files.readString(Path.of(filePath));
        assertTrue(content.contains("timestamp;activity_type;description;status;comment;time_spent_days"));
    }
    
    @Test
    @DisplayName("saveToFile should write entries to CSV")
    void testSaveToFile() throws Exception {
        String filePath = tempDir.resolve("test_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        ActivityEntry entry = new ActivityEntry(
            LocalDateTime.of(2024, 3, 15, 10, 30),
            ActivityEntry.DEV,
            "Test task",
            ActivityEntry.DONE,
            "Comment here",
            0.5
        );
        csvFile.getEntries().add(entry);
        
        csvFile.saveToFile();
        
        assertTrue(Files.exists(Path.of(filePath)));
        String content = Files.readString(Path.of(filePath));
        assertTrue(content.contains("15/03/2024 10:30"));
        assertTrue(content.contains("DEV"));
        assertTrue(content.contains("Test task"));
    }
    
    @Test
    @DisplayName("loadFromFile should read entries from CSV")
    void testLoadFromFile() throws Exception {
        String filePath = tempDir.resolve("test_tracking_20240315.csv").toString();
        Path path = Path.of(filePath);
        
        // Create file with content
        Files.writeString(path, "timestamp;activity_type;description;status;comment;time_spent_days\n" +
            "15/03/2024 10:30;DEV;Test task;DONE;Comment;0.5\n" +
            "15/03/2024 14:00;TEST;Another task;DOING;More comment;0.25");
        
        CSVFile csvFile = new CSVFile(filePath);
        csvFile.loadFromFile();
        
        assertEquals(2, csvFile.getEntries().size());
        
        ActivityEntry first = csvFile.getEntries().get(0);
        assertEquals("Test task", first.getDescription());
        assertEquals(ActivityEntry.DEV, first.getActivityType());
        assertEquals(0.5, first.getTimeSpentDays(), 0.001);
        
        ActivityEntry second = csvFile.getEntries().get(1);
        assertEquals("Another task", second.getDescription());
        assertEquals(ActivityEntry.TEST, second.getActivityType());
    }
    
    @Test
    @DisplayName("loadFromFile should create new file if not exists")
    void testLoadFromFileCreatesNew() throws Exception {
        String filePath = tempDir.resolve("nonexistent_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        csvFile.loadFromFile();
        
        assertTrue(Files.exists(Path.of(filePath)));
        assertEquals(0, csvFile.getEntries().size());
    }
    
    @Test
    @DisplayName("saveToFile should create parent directories if needed")
    void testSaveToCreatesDirectories() throws Exception {
        String filePath = tempDir.resolve("subdir/nested/test_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        ActivityEntry entry = new ActivityEntry(
            LocalDateTime.of(2024, 3, 15, 10, 30),
            ActivityEntry.DEV,
            "Test",
            ActivityEntry.DONE,
            "", 0.5
        );
        csvFile.getEntries().add(entry);
        
        csvFile.saveToFile();
        
        assertTrue(Files.exists(Path.of(filePath)));
    }
    
    @Test
    @DisplayName("getProjectName should handle path with slashes")
    void testGetProjectNameWithPath() throws Exception {
        String filePath = tempDir.resolve("log/myproject_tracking_20240315.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        assertEquals("myproject", csvFile.getProjectName());
    }
    
    @Test
    @DisplayName("getDate should return null for invalid date format")
    void testGetDateInvalidFormat() throws Exception {
        String filePath = tempDir.resolve("test_tracking_invalid.csv").toString();
        CSVFile csvFile = new CSVFile(filePath);
        
        assertNull(csvFile.getDate());
    }
}
