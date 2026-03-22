package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JDialog;

@DisplayName("SettingsDialog Tests")
class SettingsDialogTest {
    
    @Test
    @DisplayName("SettingsDialog should be created successfully")
    void testSettingsDialogCreation() {
        assertDoesNotThrow(() -> {
            new SettingsDialog(null);
        });
    }
    
    @Test
    @DisplayName("SettingsDialog should have correct title")
    void testSettingsDialogTitle() {
        SettingsDialog dialog = new SettingsDialog(null);
        assertEquals("Settings", dialog.getTitle());
    }
    
    @Test
    @DisplayName("SettingsDialog should be modal")
    void testSettingsDialogModal() {
        SettingsDialog dialog = new SettingsDialog(null);
        assertTrue(dialog.isModal());
    }
}