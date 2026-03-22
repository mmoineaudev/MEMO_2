package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;

@DisplayName("SummaryDialog Tests")
class SummaryDialogTest {
    
    @Test
    @DisplayName("SummaryDialog should be created successfully")
    void testSummaryDialogCreation() {
        assertDoesNotThrow(() -> {
            new SummaryDialog(null);
        });
    }
    
    @Test
    @DisplayName("SummaryDialog should have correct title")
    void testSummaryDialogTitle() {
        SummaryDialog dialog = new SummaryDialog(null);
        assertEquals("Activity Summary", dialog.getTitle());
    }
    
    @Test
    @DisplayName("SummaryDialog should be modal")
    void testSummaryDialogModal() {
        SummaryDialog dialog = new SummaryDialog(null);
        assertTrue(dialog.isModal());
    }
}
