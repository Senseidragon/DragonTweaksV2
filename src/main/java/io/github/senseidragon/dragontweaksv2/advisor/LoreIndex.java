package io.github.senseidragon.dragontweaksv2.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class LoreIndex {

    private static final Logger LOG = LoggerFactory.getLogger(LoreIndex.class);
    private static final String MANIFEST = "/data/dragontweaksv2/lore/lore-manifest.txt";
    private static final String LORE_ROOT = "/data/dragontweaksv2/lore/";
    private static final int MAX_INJECT = 3;

    private static final Map<String, String> ENTRIES = new LinkedHashMap<>();

    static {
        try (InputStream manifest = LoreIndex.class.getResourceAsStream(MANIFEST)) {
            if (manifest == null) {
                LOG.warn("[Advisor] lore-manifest.txt not found — lore injection disabled");
            } else {
                List<String> paths = new BufferedReader(new InputStreamReader(manifest, StandardCharsets.UTF_8))
                    .lines()
                    .map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .collect(Collectors.toList());

                int loaded = 0;
                for (String path : paths) {
                    String resourcePath = LORE_ROOT + path + ".md";
                    try (InputStream is = LoreIndex.class.getResourceAsStream(resourcePath)) {
                        if (is == null) {
                            LOG.warn("[Advisor] lore entry not found: {}", resourcePath);
                            continue;
                        }
                        String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        String name = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
                        ENTRIES.put(name, stripFrontmatter(raw));
                        loaded++;
                    } catch (Exception e) {
                        LOG.warn("[Advisor] failed to load lore entry {}: {}", path, e.getMessage());
                    }
                }
                LOG.info("[Advisor] lore index loaded — {} entries", loaded);
            }
        } catch (Exception e) {
            LOG.warn("[Advisor] lore index load failed: {}", e.getMessage());
        }
    }

    private LoreIndex() {}

    public static String inject(String playerMessage) {
        if (ENTRIES.isEmpty()) return "";
        String lower = playerMessage.toLowerCase(Locale.ROOT);
        List<String> matched = new ArrayList<>();
        List<String> matchedNames = new ArrayList<>();

        for (Map.Entry<String, String> entry : ENTRIES.entrySet()) {
            if (matched.size() >= MAX_INJECT) break;
            if (matches(lower, entry.getKey())) {
                matched.add(entry.getValue());
                matchedNames.add(entry.getKey());
            }
        }

        if (matched.isEmpty()) return "";
        LOG.info("[Advisor] lore matched: {}", String.join(", ", matchedNames));
        return "## Lore\n" + String.join("\n\n---\n\n", matched) + "\n\n";
    }

    private static boolean matches(String message, String name) {
        String normalized = name.replace("_", " ");
        return Pattern.compile("(?i)\\b" + Pattern.quote(normalized) + "\\b").matcher(message).find();
    }

    private static String stripFrontmatter(String content) {
        if (!content.startsWith("---")) return content;
        int end = content.indexOf("---", 3);
        if (end == -1) return content;
        return content.substring(end + 3).stripLeading();
    }
}
