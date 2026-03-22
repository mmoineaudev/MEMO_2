package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;
import com.memo_v2.model.CSVFile;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;

@DisplayName("ExportDialog Tests")
class ExportDialogTest {
    
    @Test
    @DisplayName("ExportDialog should be created successfully")
    void testExportDialogCreation() {
        assertDoesNotThrow(() -> {
            new ExportDialog(null, null);
        });
    }
    
    @Test
    @DisplayName("ExportDialog should have correct title")
    void testExportDialogTitle() {
        ExportDialog dialog = new ExportDialog(null, null);
        assertEquals("Export Entries", dialog.getTitle());
    }
    
    @Test
    @DisplayName("ExportDialog should be modal")
    void testExportDialogModal() {
        ExportDialog dialog = new ExportDialog(null, null);
        assertTrue(dialog.isModal());
    }
}
