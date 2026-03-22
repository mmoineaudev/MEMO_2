package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;

@DisplayName("SearchDialog Tests")
class SearchDialogTest {
    
    @Test
    @DisplayName("SearchDialog should be created successfully")
    void testSearchDialogCreation() {
        assertDoesNotThrow(() -> {
            new SearchDialog(null);
        });
    }
    
    @Test
    @DisplayName("SearchDialog should have correct title")
    void testSearchDialogTitle() {
        SearchDialog dialog = new SearchDialog(null);
        assertEquals("Search Activity Entries", dialog.getTitle());
    }
    
    @Test
    @DisplayName("SearchDialog should be modal")
    void testSearchDialogModal() {
        SearchDialog dialog = new SearchDialog(null);
        assertTrue(dialog.isModal());
    }
}
