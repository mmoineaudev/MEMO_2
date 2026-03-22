package com.memo_v2.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("ActivityEntry Tests")
class ActivityEntryTest {
    
    @Test
    @DisplayName("Constructor should initialize all fields correctly")
    void testConstructor() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 10, 30);
        ActivityEntry entry = new ActivityEntry(
            timestamp,
            ActivityEntry.DEV,
            "Test description",
            ActivityEntry.DOING,
            "Test comment",
            0.5
        );
        
        assertEquals(timestamp, entry.getTimestamp());
        assertEquals(ActivityEntry.DEV, entry.getActivityType());
        assertEquals("Test description", entry.getDescription());
        assertEquals(ActivityEntry.DOING, entry.getStatus());
        assertEquals("Test comment", entry.getComment());
        assertEquals(0.5, entry.getTimeSpentDays(), 0.001);
    }
    
    @Test
    @DisplayName("Default constructor should create empty entry")
    void testDefaultConstructor() {
        ActivityEntry entry = new ActivityEntry();
        
        assertNull(entry.getTimestamp());
        assertNull(entry.getActivityType());
        assertNull(entry.getDescription());
        assertNull(entry.getStatus());
        assertNull(entry.getComment());
        assertEquals(0.0, entry.getTimeSpentDays(), 0.001);
    }
    
    @Test
    @DisplayName("getTimeSpentHours should convert days to hours correctly")
    void testGetTimeSpentHours() {
        ActivityEntry entry = new ActivityEntry();
        entry.setTimeSpentDays(0.5); // 0.5 days = 3.875 hours
        
        assertEquals(3.875, entry.getTimeSpentHours(), 0.001);
    }
    
    @Test
    @DisplayName("getTimestampFormatted should format timestamp correctly")
    void testGetTimestampFormatted() {
        ActivityEntry entry = new ActivityEntry();
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 10, 30);
        entry.setTimestamp(timestamp);
        
        assertEquals("15/03/2024 10:30", entry.getTimestampFormatted());
    }
    
    @Test
    @DisplayName("getDateOnlyFormatted should format date correctly")
    void testGetDateOnlyFormatted() {
        ActivityEntry entry = new ActivityEntry();
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 10, 30);
        entry.setTimestamp(timestamp);
        
        assertEquals("20240315", entry.getDateOnlyFormatted());
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void testToString() {
        ActivityEntry entry = new ActivityEntry(
            LocalDateTime.of(2024, 3, 15, 10, 30),
            ActivityEntry.DEV,
            "Test task",
            ActivityEntry.DONE,
            "Completed successfully",
            0.25
        );
        
        String result = entry.toString();
        assertTrue(result.contains("15/03/2024 10:30"));
        assertTrue(result.contains("DEV"));
        assertTrue(result.contains("Test task"));
        assertTrue(result.contains("DONE"));
        assertTrue(result.contains("Completed successfully"));
    }
    
    @Test
    @DisplayName("Setters should update values correctly")
    void testSetters() {
        ActivityEntry entry = new ActivityEntry();
        
        entry.setTimestamp(LocalDateTime.of(2024, 3, 15, 10, 30));
        entry.setActivityType(ActivityEntry.TEST);
        entry.setDescription("New description");
        entry.setStatus(ActivityEntry.TODO);
        entry.setComment("New comment");
        entry.setTimeSpentDays(0.75);
        
        assertEquals(LocalDateTime.of(2024, 3, 15, 10, 30), entry.getTimestamp());
        assertEquals(ActivityEntry.TEST, entry.getActivityType());
        assertEquals("New description", entry.getDescription());
        assertEquals(ActivityEntry.TODO, entry.getStatus());
        assertEquals("New comment", entry.getComment());
        assertEquals(0.75, entry.getTimeSpentDays(), 0.001);
    }
    
    @Test
    @DisplayName("Activity type constants should have correct values")
    void testActivityTypeConstants() {
        assertEquals("DEV", ActivityEntry.DEV);
        assertEquals("TEST", ActivityEntry.TEST);
        assertEquals("CEREMONY", ActivityEntry.CEREMONY);
        assertEquals("LEARNING", ActivityEntry.LEARNING);
        assertEquals("CONTINUOUS_IMPROVEMENT", ActivityEntry.CONTINUOUS_IMPROVEMENT);
        assertEquals("SUPPORT", ActivityEntry.SUPPORT);
        assertEquals("ADMIN", ActivityEntry.ADMIN);
        assertEquals("DOCUMENTATION", ActivityEntry.DOCUMENTATION);
    }
    
    @Test
    @DisplayName("Status constants should have correct values")
    void testStatusConstants() {
        assertEquals("TODO", ActivityEntry.TODO);
        assertEquals("DOING", ActivityEntry.DOING);
        assertEquals("DONE", ActivityEntry.DONE);
        assertEquals("NOTE", ActivityEntry.NOTE);
    }
}
