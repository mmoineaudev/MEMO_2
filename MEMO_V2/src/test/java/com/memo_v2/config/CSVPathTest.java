package com.memo_v2.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class CSVPathTest {
    public static void main(String[] args) throws Exception {
        // Create a temporary directory for testing
        Path tempDir = Files.createTempDirectory("memo_test");
        System.out.println("Test directory: " + tempDir);
        
        // Test 1: Default path resolution
        System.out.println("\n=== Test 1: Default path ===");
        String defaultPath = ConfigManager.getStorageDirectory();
        System.out.println("Config returns: " + defaultPath);
        File testFile1 = new File(defaultPath);
        if (defaultPath.startsWith("./")) {
            File projectRoot = new File("..").getAbsoluteFile().getParentFile();
            String resolved = new File(projectRoot, defaultPath).getPath();
            System.out.println("Resolved to: " + resolved);
            testFile1 = new File(resolved);
        }
        System.out.println("Final path exists: " + testFile1.exists());
        
        // Test 2: Absolute path handling
        System.out.println("\n=== Test 2: Absolute path ===");
        String absolutePath = tempDir.toString();
        ConfigManager.setStorageDirectory(absolutePath);
        String retrieved = ConfigManager.getStorageDirectory();
        System.out.println("Stored: " + absolutePath);
        System.out.println("Retrieved: " + retrieved);
        File testFile2 = new File(retrieved);
        if (retrieved.startsWith("./")) {
            File projectRoot = new File("..").getAbsoluteFile().getParentFile();
            String resolved = new File(projectRoot, retrieved).getPath();
            System.out.println("Resolved to: " + resolved);
            testFile2 = new File(resolved);
        }
        System.out.println("Final path exists: " + testFile2.exists());
        
        // Test 3: Relative path handling
        System.out.println("\n=== Test 3: Relative path ===");
        String relativePath = "./log";
        ConfigManager.setStorageDirectory(relativePath);
        retrieved = ConfigManager.getStorageDirectory();
        System.out.println("Stored: " + relativePath);
        System.out.println("Retrieved: " + retrieved);
        File testFile3 = new File(retrieved);
        if (retrieved.startsWith("./")) {
            File projectRoot = new File("..").getAbsoluteFile().getParentFile();
            String resolved = new File(projectRoot, retrieved).getPath();
            System.out.println("Resolved to: " + resolved);
            testFile3 = new File(resolved);
        }
        System.out.println("Final path exists: " + testFile3.exists());
        
        // Cleanup
        deleteRecursively(tempDir.toFile());
        System.out.println("\n=== All tests completed ===");
    }
    
    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
