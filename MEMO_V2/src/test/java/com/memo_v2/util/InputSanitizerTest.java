package com.memo_v2.util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class InputSanitizerTest {
    
    @Test
    public void testNoSemicolons() {
        String input = "Normal text without semicolons";
        String result = InputSanitizer.sanitize(input);
        assertEquals(input, result, "Input without semicolons should remain unchanged");
    }
    
    @Test
    public void testSingleSemicolon() {
        String input = "This has; a semicolon";
        String expected = "This has! a semicolon";
        assertEquals(expected, InputSanitizer.sanitize(input));
    }
    
    @Test
    public void testMultipleSemicolons() {
        String input = "First; second; third; fourth";
        String expected = "First! second! third! fourth";
        assertEquals(expected, InputSanitizer.sanitize(input));
    }
    
    @Test
    public void testEmptyString() {
        assertEquals("", InputSanitizer.sanitize(""));
    }
    
    @Test
    public void testNullInput() {
        assertNull(InputSanitizer.sanitize(null));
    }
    
    @Test
    public void testOnlySemicolons() {
        String input = ";;;";
        String expected = "!!!";
        assertEquals(expected, InputSanitizer.sanitize(input));
    }
    
    @Test
    public void testMixedContent() {
        String input = "DEV; testing; this feature!";
        String expected = "DEV! testing! this feature!";
        assertEquals(expected, InputSanitizer.sanitize(input));
    }
}