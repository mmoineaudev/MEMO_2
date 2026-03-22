package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;

@DisplayName("DateRangeDialog Tests")
class DateRangeDialogTest {
    
    @Test
    @DisplayName("DateRangeDialog should be created successfully")
    void testDateRangeDialogCreation() {
        assertDoesNotThrow(() -> {
            new DateRangeDialog(null);
        });
    }
    
    @Test
    @DisplayName("DateRangeDialog should have correct title")
    void testDateRangeDialogTitle() {
        DateRangeDialog dialog = new DateRangeDialog(null);
        assertEquals("Filter by Date Range", dialog.getTitle());
    }
    
    @Test
    @DisplayName("DateRangeDialog should be modal")
    void testDateRangeDialogModal() {
        DateRangeDialog dialog = new DateRangeDialog(null);
        assertTrue(dialog.isModal());
    }
}
