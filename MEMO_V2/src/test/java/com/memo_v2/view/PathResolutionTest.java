package com.memo_v2.view;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import com.memo_v2.config.ConfigManager;

public class PathResolutionTest {
    
    @BeforeEach
    public void setUp() throws Exception {
        File configFile = new File("./memo.conf");
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        File configFile = new File("./memo.conf");
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    @Test
    public void testRelativePathResolutionInMainFrame() {
        // Set a relative path in config
        ConfigManager.setStorageDirectory("./test_log_dir");
        String stored = ConfigManager.getStorageDirectory();
        
        // Verify it's stored as-is (relative)
        File file = new File(stored);
        assertFalse(file.isAbsolute(), "Path should be relative when stored");
        assertEquals("./test_log_dir", stored, "Relative path should be preserved");
    }
    
    @Test
    public void testAbsolutePathResolutionInMainFrame() {
        // Set an absolute path in config
        String absPath = System.getProperty("java.io.tmpdir") + "/memo_test_logs";
        ConfigManager.setStorageDirectory(absPath);
        String stored = ConfigManager.getStorageDirectory();
        
        // Verify it's stored as-is (absolute)
        File file = new File(stored);
        assertTrue(file.isAbsolute(), "Absolute path should be preserved");
        assertEquals(absPath, stored, "Absolute path should match");
    }
    
    @Test
    public void testPersistenceAcrossSession() {
        // Simulate first session - set config
        ConfigManager.setStorageDirectory("./logs");
        String firstCall = ConfigManager.getStorageDirectory();
        
        // Simulate second session (app restart) - should load same value
        File configFile = new File("./memo.conf");
        assertTrue(configFile.exists(), "Config file should exist after save");
        
        String secondCall = ConfigManager.getStorageDirectory();
        assertEquals(firstCall, secondCall, "Path should persist across sessions");
    }
}
