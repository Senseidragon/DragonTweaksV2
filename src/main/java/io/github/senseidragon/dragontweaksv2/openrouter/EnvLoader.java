package io.github.senseidragon.dragontweaksv2.openrouter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class EnvLoader {

    private EnvLoader() {}

    public static Map<String, String> load(Path envFile) throws IOException {
        Map<String, String> result = new HashMap<>();
        for (String line : Files.readAllLines(envFile)) {
            String trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int eq = trimmed.indexOf('=');
            if (eq < 1) continue;
            result.put(trimmed.substring(0, eq).strip(), trimmed.substring(eq + 1).strip());
        }
        return result;
    }
}
