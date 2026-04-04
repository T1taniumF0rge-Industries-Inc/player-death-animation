// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.config;

import com.playerdeathanimation.animation.DefaultKneelFallAnimation;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PDAConfig {
    private static final String FILE_NAME = "pda-config.toml";
    private static final PDAConfigData DATA = new PDAConfigData();

    private PDAConfig() {
    }

    public static PDAConfigData get() {
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
                    case "enable_self_death_effect" -> DATA.enableSelfDeathEffect = Boolean.parseBoolean(value);
                    case "enable_observer_animation" -> DATA.enableObserverAnimation = Boolean.parseBoolean(value);
                    case "observer_animation" -> DATA.observerAnimation = value;
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
            lines.add("# Player Death Animation config");
            lines.add("enable_self_death_effect = " + DATA.enableSelfDeathEffect);
            lines.add("enable_observer_animation = " + DATA.enableObserverAnimation);
            lines.add("observer_animation = \"" + DATA.observerAnimation + "\"");
            Files.write(path, lines);
        } catch (IOException ignored) {
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static class PDAConfigData {
        public boolean enableSelfDeathEffect = true;
        public boolean enableObserverAnimation = true;
        public String observerAnimation = DefaultKneelFallAnimation.ID;
    }
}
