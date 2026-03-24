package com.memo_v2.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "./memo.conf";
    private static final String STORAGE_DIR_KEY = "storageDirectory";
    
    public static String getStorageDirectory() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                // Create default config file
                Properties props = new Properties();
                props.setProperty(STORAGE_DIR_KEY, "./log");
                try (OutputStream output = new FileOutputStream(configFile)) {
                    props.store(output, "MEMO_V2 Configuration");
                }
            }
            
            Properties props = new Properties();
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
                return props.getProperty(STORAGE_DIR_KEY, "./log");
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
        return "./log";
    }
    
    public static void setStorageDirectory(String directory) {
        try {
            File configFile = new File(CONFIG_FILE);
            Properties props = new Properties();
            if (configFile.exists()) {
                try (InputStream input = new FileInputStream(configFile)) {
                    props.load(input);
                }
            }
            props.setProperty(STORAGE_DIR_KEY, directory);
            try (OutputStream output = new FileOutputStream(configFile)) {
                props.store(output, "MEMO_V2 Configuration");
            }
        } catch (Exception e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
