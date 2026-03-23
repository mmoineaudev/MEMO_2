package com.memo_v2.util;

/**
 * Utility class for sanitizing user input before CSV storage.
 * Replaces semicolons with exclamation marks to prevent delimiter conflicts.
 */
public class InputSanitizer {
    
    private static final char SEMICOLON = ';';
    private static final char REPLACEMENT = '!';
    
    /**
     * Sanitizes input by replacing semicolons with exclamation marks.
     * This prevents delimiter conflicts in CSV storage.
     * 
     * @param input the raw user input
     * @return sanitized string with semicolons replaced
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replace(";", String.valueOf(REPLACEMENT));
    }
}
