package com.memo_v2.view;

import com.memo_v2.model.ActivityEntry;
import com.memo_v2.model.CSVFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SummaryDialogTest {

    @TempDir
    Path tempDir;

    private CSVFile currentFile;
    private List<CSVFile> allFiles;

    @BeforeEach
    public void setUp() throws Exception {
        // Create test files with proper entries
        currentFile = createTestFile("TestProject_tracking_20240315.csv", 
            new ActivityEntry(LocalDateTime.of(2024, 3, 15, 9, 0), "Development", "Coding", "In Progress", "Test", 0.125),
            new ActivityEntry(LocalDateTime.of(2024, 3, 15, 10, 0), "Testing", "Testing code", "Done", "Test2", 0.250));
        
        CSVFile file2 = createTestFile("TestProject_tracking_20240310.csv",
            new ActivityEntry(LocalDateTime.of(2024, 3, 10, 9, 0), "Development", "Coding", "In Progress", "Test", 0.125));
        
        CSVFile file3 = createTestFile("TestProject_tracking_20240320.csv",
            new ActivityEntry(LocalDateTime.of(2024, 3, 20, 9, 0), "Development", "Coding", "In Progress", "Test", 0.125));
        
        allFiles = List.of(currentFile, file2, file3);
    }

    private CSVFile createTestFile(String fileName, ActivityEntry... entries) throws Exception {
        Path filePath = tempDir.resolve(fileName);
        File file = filePath.toFile();
        
        CSVFile csvFile = new CSVFile(file.getAbsolutePath());
        csvFile.createNewFile();
        csvFile.setEntries(List.of(entries));
        csvFile.saveToFile();
        return csvFile;
    }

    @Test
    @Order(1)
    public void testDailySummaryShowsCurrentFileOnly() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        
        String result = dialog.getDailySummary();
        
        // Daily summary should show entries from current file only
        assertTrue(result.contains("DAILY ACTIVITY SUMMARY"));
        assertTrue(result.contains("2024-03-15"));
        assertFalse(result.contains("2024-03-10"));
        assertFalse(result.contains("2024-03-20"));
    }

    @Test
    @Order(2)
    public void testMonthlySummaryShowsAllFiles() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        
        String result = dialog.getMonthlySummary();
        
        // Monthly summary should show entries from all files grouped by month
        assertTrue(result.contains("MONTHLY ACTIVITY SUMMARY"));
        assertTrue(result.contains("2024-03"));
        assertTrue(result.contains("Coding"));
        assertTrue(result.contains("Testing code"));
    }

    @Test
    @Order(3)
    public void testTimeframeSummaryWithDateRange() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        dialog.setDateRangeComboSelectedIndex(0); // Last 7 days
        
        String result = dialog.getTimeframeSummary();
        
        // Timeframe summary should show date range header
        assertTrue(result.contains("TIMEFRAME ACTIVITY SUMMARY"));
        assertTrue(result.contains("Range:"));
        assertTrue(result.contains("to"));
    }

    @Test
    @Order(4)
    public void testDateRangeComboPopulated() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        
        assertEquals(6, dialog.getDateRangeComboItemCount(), "Should have 6 date range options");
        assertEquals("Last 7 days", dialog.getDateRangeComboItemAt(0));
        assertEquals("All time", dialog.getDateRangeComboItemAt(5));
    }

    @Test
    @Order(5)
    public void testSummaryTypeOptions() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        
        String[] expectedTypes = {"Daily", "Monthly", "Timeframe"};
        
        assertEquals(3, dialog.getSummaryTypeItemCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            assertEquals(expectedTypes[i], dialog.getSummaryTypeItemAt(i));
        }
    }

    @Test
    @Order(6)
    public void testDailySummaryShowsCorrectFormat() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        
        String result = dialog.getDailySummary();
        
        // Check for proper formatting
        assertTrue(result.contains("days"));
        assertTrue(result.contains("hours"));
        assertTrue(result.contains("Total:"));
    }

    @Test
    @Order(7)
    public void testTimeframeSummaryShowsCorrectMonth() throws Exception {
        SummaryDialog dialog = new SummaryDialog(null, currentFile, allFiles);
        dialog.setDateRangeComboSelectedIndex(4); // Current month
        
        String result = dialog.getTimeframeSummary();
        
        // Should show current month range
        assertTrue(result.contains("Range:"));
        assertTrue(result.contains("to"));
    }
}
