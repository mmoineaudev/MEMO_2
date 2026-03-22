package com.memo_v2.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JFrame;

@DisplayName("MainFrame Tests")
class MainFrameTest {
    
    @Test
    @DisplayName("MainFrame should be created successfully")
    void testMainFrameCreation() {
        assertDoesNotThrow(() -> {
            new MainFrame();
        });
    }
    
    @Test
    @DisplayName("MainFrame should have correct title")
    void testMainFrameTitle() {
        MainFrame frame = new MainFrame();
        assertEquals("MEMO_V2 - Activity Tracker", frame.getTitle());
    }
    
    @Test
    @DisplayName("MainFrame should have default size")
    void testMainFrameSize() {
        MainFrame frame = new MainFrame();
        assertEquals(1200, frame.getWidth());
        assertEquals(800, frame.getHeight());
    }
    
    @Test
    @DisplayName("MainFrame should have exit on close")
    void testMainFrameCloseOperation() {
        MainFrame frame = new MainFrame();
        assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation());
    }
}
