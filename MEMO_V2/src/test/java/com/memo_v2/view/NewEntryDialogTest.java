package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;
import com.memo_v2.model.CSVFile;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;

@DisplayName("NewEntryDialog Tests")
class NewEntryDialogTest {
    
    @Test
    @DisplayName("NewEntryDialog should be created successfully")
    void testNewEntryDialogCreation() {
        assertDoesNotThrow(() -> {
            new NewEntryDialog(null, null);
        });
    }
    
    @Test
    @DisplayName("NewEntryDialog should have correct title")
    void testNewEntryDialogTitle() {
        NewEntryDialog dialog = new NewEntryDialog(null, null);
        assertEquals("New Activity Entry", dialog.getTitle());
    }
    
    @Test
    @DisplayName("NewEntryDialog should be modal")
    void testNewEntryDialogModal() {
        NewEntryDialog dialog = new NewEntryDialog(null, null);
        assertTrue(dialog.isModal());
    }
}
