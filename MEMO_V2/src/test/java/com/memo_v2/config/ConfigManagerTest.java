package com.memo_v2.config;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Files;

public class ConfigManagerTest {
    
    @BeforeEach
    public void setUp() throws Exception {
        // Clean up any existing config file before each test
        File configFile = new File("./memo.conf");
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        // Clean up config file after tests
        File configFile = new File("./memo.conf");
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    @Test
    public void testDefaultStorageDirectoryWhenNoConfigExists() {
        String dir = ConfigManager.getStorageDirectory();
        assertEquals("./log", dir);
    }
    
    @Test
    public void testSetAndGetStorageDirectory() {
        String customPath = "/custom/path/to/logs";
        ConfigManager.setStorageDirectory(customPath);
        String retrieved = ConfigManager.getStorageDirectory();
        assertEquals(customPath, retrieved);
    }
    
    @Test
    public void testRelativePathPreserved() {
        // Set a relative path - it should be preserved as-is (not converted)
        ConfigManager.setStorageDirectory("./custom_logs");
        String retrieved = ConfigManager.getStorageDirectory();
        File file = new File(retrieved);
        assertFalse(file.isAbsolute(), "Config should preserve relative paths");
    }
    
    @Test
    public void testPersistenceAcrossCalls() {
        // First call sets the value
        ConfigManager.setStorageDirectory("./test_storage");
        String firstCall = ConfigManager.getStorageDirectory();
        
        // Second call should return same value (simulating app restart)
        String secondCall = ConfigManager.getStorageDirectory();
        assertEquals(firstCall, secondCall);
    }
}
