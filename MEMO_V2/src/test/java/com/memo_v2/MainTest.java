package com.memo_v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Main Tests")
class MainTest {
    
    @Test
    @DisplayName("Main class should compile and have main method")
    void testMainExists() {
        // Just verify the class exists and has a main method
        assertNotNull(Main.class);
        assertDoesNotThrow(() -> {
            Main.class.getDeclaredMethod("main", String[].class);
        });
    }
}
