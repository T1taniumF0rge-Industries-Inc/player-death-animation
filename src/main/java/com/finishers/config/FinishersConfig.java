// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.config;

import com.titan1um.finishers.animation.DefaultKneelFallAnimation;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FinishersConfig {
    private static final String FILE_NAME = "finishers-config.toml";
    private static final FinishersConfigData DATA = new FinishersConfigData();

    private FinishersConfig() {
    }

    public static FinishersConfigData get() {
        return DATA;
    }

    public static void load() {
        Path path = configPath();

        if (!Files.exists(path)) {
            save();
            return;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                String normalized = line.trim();
                if (normalized.isEmpty() || normalized.startsWith("#")) {
                    continue;
                }
                String[] split = normalized.split("=", 2);
                if (split.length != 2) {
                    continue;
                }

                String key = split[0].trim();
                String value = split[1].trim().replace("\"", "");

                switch (key) {
                    case "enable_self_black_screen_death_effect" -> DATA.enableSelfBlackScreenDeathEffect = Boolean.parseBoolean(value);
                    case "enable_finishers" -> DATA.enableFinishers = Boolean.parseBoolean(value);
                    case "finisher_type" -> DATA.finisherType = value;
                    default -> {
                    }
                }
            }
        } catch (IOException ignored) {
            save();
        }
    }

    public static void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Finishers config");
            lines.add("enable_self_black_screen_death_effect = " + DATA.enableSelfBlackScreenDeathEffect);
            lines.add("enable_finishers = " + DATA.enableFinishers);
            lines.add("finisher_type = \"" + DATA.finisherType + "\"");
            Files.write(path, lines);
        } catch (IOException ignored) {
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static class FinishersConfigData {
        public boolean enableSelfBlackScreenDeathEffect = true;
        public boolean enableFinishers = true;
        public String finisherType = DefaultKneelFallAnimation.ID;
    }
}
