package io.github.senseidragon.dragontweaksv2.advisor.tools;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScanAreaToolTest {

    @Test
    void largeCaveIsTheOpenEndedTopBucket() {
        ScanAreaTool tool = new ScanAreaTool();
        assertEquals("Large cave", tool.classifyVoid(1000));
        assertEquals("Large cave", tool.classifyVoid(2197));
        assertEquals("Large cave", tool.classifyVoid(5000));
        assertEquals("Large cave", tool.classifyVoid(10_000));
    }

    @Test
    void massiveCavernCategoryNoLongerExists() {
        ScanAreaTool tool = new ScanAreaTool();
        for (int volume : new int[]{0, 199, 200, 499, 500, 999, 1000, 5000, 50_000}) {
            assertNotEquals("Massive cavern", tool.classifyVoid(volume));
        }
    }

    @Test
    void lowerBucketsUnchanged() {
        ScanAreaTool tool = new ScanAreaTool();
        assertEquals("Large tunnel", tool.classifyVoid(0));
        assertEquals("Large tunnel", tool.classifyVoid(199));
        assertEquals("Small cave", tool.classifyVoid(200));
        assertEquals("Small cave", tool.classifyVoid(499));
        assertEquals("Dungeon room", tool.classifyVoid(500));
        assertEquals("Dungeon room", tool.classifyVoid(999));
    }

    // ── lightBucket ───────────────────────────────────────────────────────────

    @Test
    void lightBucketCoversAllLevels() {
        assertEquals("dark",     ScanAreaTool.lightBucket(0));
        assertEquals("dim",      ScanAreaTool.lightBucket(1));
        assertEquals("dim",      ScanAreaTool.lightBucket(3));
        assertEquals("low",      ScanAreaTool.lightBucket(4));
        assertEquals("low",      ScanAreaTool.lightBucket(6));
        assertEquals("moderate", ScanAreaTool.lightBucket(7));
        assertEquals("moderate", ScanAreaTool.lightBucket(10));
        assertEquals("well-lit", ScanAreaTool.lightBucket(11));
        assertEquals("well-lit", ScanAreaTool.lightBucket(13));
        assertEquals("bright",   ScanAreaTool.lightBucket(14));
        assertEquals("bright",   ScanAreaTool.lightBucket(15));
    }

    // ── summarizeLightSources ─────────────────────────────────────────────────

    @Test
    void noLightSourcesReturnsEmpty() {
        assertEquals("", ScanAreaTool.summarizeLightSources(List.of()));
    }

    @Test
    void singleLightSourceFormatsCorrectly() {
        String result = ScanAreaTool.summarizeLightSources(List.of("torch (above)"));
        assertEquals("torch x1 (above)", result);
    }

    @Test
    void multipleOfSameTypeDeduplicatesPositions() {
        // Two torches above, one at level — positions deduplicated, count is 3
        String result = ScanAreaTool.summarizeLightSources(
            List.of("torch (above)", "torch (above)", "torch (at level)"));
        assertTrue(result.contains("torch x3"), "expected count=3, got: " + result);
        assertTrue(result.contains("above"),    "expected 'above' in positions");
        assertTrue(result.contains("at level"), "expected 'at level' in positions");
    }

    @Test
    void differentTypesAppearAsSeparateParts() {
        String result = ScanAreaTool.summarizeLightSources(
            List.of("torch (above)", "lava (below)"));
        assertTrue(result.contains("torch x1"), result);
        assertTrue(result.contains("lava x1"),  result);
        // Each type is its own segment separated by "; "
        assertTrue(result.contains("; "), "expected segments separated by '; '");
    }

    @Test
    void lightingSummaryLineIsModelReadable() {
        // Verify the format the model receives does not look like raw data keys or JSON.
        // The line must be plain English-adjacent: a label, a bucket word, and optional sources.
        String summary = ScanAreaTool.summarizeLightSources(
            List.of("torch (above)", "glow lichen (below)"));
        assertFalse(summary.isEmpty());
        assertFalse(summary.contains("{"), "should not contain JSON braces");
        assertFalse(summary.contains("\""), "should not contain JSON quotes");
        // Each entry must have a readable label and a parenthesised position
        assertTrue(summary.matches(".*\\w+ x\\d+ \\(.*\\).*"),
            "expected 'type xN (position)' pattern, got: " + summary);
    }
}
