package com.example.bankcards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {

    @Test
    public void testBasicMath() {
        assertEquals(2 + 2, 4);
        assertTrue(5 > 3);
        assertFalse(1 > 2);
    }

    @Test
    public void testStringOperations() {
        String testString = "Hello World";
        assertEquals("Hello World", testString);
        assertTrue(testString.contains("World"));
    }

    @Test
    public void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);
    }
}
