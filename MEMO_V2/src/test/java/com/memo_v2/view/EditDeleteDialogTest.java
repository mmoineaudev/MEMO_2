package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;

@DisplayName("EditDeleteDialog Tests")
class EditDeleteDialogTest {
    
    @Test
    @DisplayName("EditDeleteDialog should be created successfully")
    void testEditDeleteDialogCreation() {
        assertDoesNotThrow(() -> {
            new EditDeleteDialog(null, null, null);
        });
    }
    
    @Test
    @DisplayName("EditDeleteDialog should have correct title")
    void testEditDeleteDialogTitle() {
        EditDeleteDialog dialog = new EditDeleteDialog(null, null, null);
        assertEquals("Edit Entry", dialog.getTitle());
    }
    
    @Test
    @DisplayName("EditDeleteDialog should be modal")
    void testEditDeleteDialogModal() {
        EditDeleteDialog dialog = new EditDeleteDialog(null, null, null);
        assertTrue(dialog.isModal());
    }
}