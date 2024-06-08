package io.zmeu.Frontend.Parser.Literals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PathIdentifierTest {
    @Test
    void testPathIdentifier() {
        var id = PathIdentifier.of("Test");
        assertEquals("Test", id.getSymbol());
        assertArrayEquals(new String[]{"Test"}, id.getPaths());
    }

    @Test
    void testPathIdentifierMultiple() {
        var id = PathIdentifier.of("Test.VM");
        assertEquals("Test.VM", id.getSymbol());
        assertArrayEquals(new String[]{"Test", "VM"}, id.getPaths());
    }

}