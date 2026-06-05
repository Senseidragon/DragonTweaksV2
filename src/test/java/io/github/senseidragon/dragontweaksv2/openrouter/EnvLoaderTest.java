package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvLoaderTest {

    @TempDir Path tempDir;

    @Test
    void loadsKeyValuePairs() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "OPENROUTER_API_KEY=my-key\nOTHER=value\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertEquals("my-key", env.get("OPENROUTER_API_KEY"));
        assertEquals("value", env.get("OTHER"));
    }

    @Test
    void throwsWhenFileMissing() {
        assertThrows(IOException.class, () -> EnvLoader.load(tempDir.resolve(".env")));
    }

    @Test
    void returnsMapWithoutAbsentKey() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "OTHER=value\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertFalse(env.containsKey("OPENROUTER_API_KEY"));
    }

    @Test
    void ignoresMalformedAndCommentLines() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "# comment\n\nBAD_LINE\nGOOD=yes\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertEquals("yes", env.get("GOOD"));
        assertEquals(1, env.size());
    }
}
