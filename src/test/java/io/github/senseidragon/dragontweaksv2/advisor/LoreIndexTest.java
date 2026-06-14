package io.github.senseidragon.dragontweaksv2.advisor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LoreIndex.inject() using the classpath lore files.
 *
 * LoreIndex uses a static initializer and a static final class pattern —
 * there is no instance to construct. All tests go through LoreIndex.inject().
 */
class LoreIndexTest {

    @Test
    void queryMatchesSingleKeyword() {
        String result = LoreIndex.inject("an enderman attacked me");
        assertFalse(result.isEmpty(), "Expected enderman lore to match");
        assertTrue(result.contains("## Lore"), "Expected lore block header");
    }

    @Test
    void queryNoMatchReturnsEmpty() {
        String result = LoreIndex.inject("xyzzy foobar baz");
        assertTrue(result.isEmpty(), "Expected no match for unknown words");
    }

    @Test
    void queryCaseInsensitive() {
        String lower = LoreIndex.inject("enderman");
        String upper = LoreIndex.inject("ENDERMAN");
        // Both should either match or both not match; content must be equal
        assertEquals(lower, upper, "Expected case-insensitive matching");
    }

    @Test
    void queryMultipleKeywordsReturnsDedupedResults() {
        // "enderman" and "creeper" both appear in the lore; should return two distinct sections
        String result = LoreIndex.inject("the enderman and creeper chased me");
        // Should match at least one entry
        assertFalse(result.isEmpty(), "Expected at least one lore match");
        // Duplicate content should not appear — split on separator and check distinctness
        if (result.contains("---")) {
            String[] sections = result.split("\n---\n");
            long distinct = java.util.Arrays.stream(sections).distinct().count();
            assertEquals(sections.length, distinct, "Duplicate lore sections returned");
        }
    }
}
