// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.config;

import com.titan1um.finishers.animation.DeathAnimationRegistry;
import com.titan1um.finishers.animation.DefaultKneelFallAnimation;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FinishersConfig {
    public static final String FINISHER_ANIMATION_OFF = "off";
    public static final String FINISHER_SOUND_OFF = "off";
    public static final String FINISHER_SOUND_MATCH = "match";
    public static final String FINISHER_SOUND_CUSTOM = "custom";

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
                    case "play_finishers" -> DATA.playFinishers = Boolean.parseBoolean(value);
                    case "finisher_animation" -> DATA.finisherAnimation = value;
                    case "finisher_sound" -> DATA.finisherSound = value;
                    case "custom_finisher_sound" -> DATA.customFinisherSound = value;
                    case "last_finisher_animation" -> DATA.lastFinisherAnimation = value;
                    case "last_finisher_sound" -> DATA.lastFinisherSound = value;
                    default -> {
                    }
                }
            }

            sanitizeAfterLoad();
        } catch (IOException ignored) {
            save();
        }
    }

    private static void sanitizeAfterLoad() {
        if (!DeathAnimationRegistry.availableAnimationIds().contains(DATA.finisherAnimation)) {
            DATA.finisherAnimation = DefaultKneelFallAnimation.ID;
        }

        if (!DeathAnimationRegistry.availableAnimationIds().contains(DATA.lastFinisherAnimation)
            || FINISHER_ANIMATION_OFF.equals(DATA.lastFinisherAnimation)) {
            DATA.lastFinisherAnimation = DefaultKneelFallAnimation.ID;
        }

        if (!availableSoundIds().contains(DATA.finisherSound)) {
            DATA.finisherSound = FINISHER_SOUND_MATCH;
        }

        if (!availableSoundIds().contains(DATA.lastFinisherSound)
            || FINISHER_SOUND_OFF.equals(DATA.lastFinisherSound)) {
            DATA.lastFinisherSound = FINISHER_SOUND_MATCH;
        }

        if (DATA.customFinisherSound.isBlank()) {
            DATA.customFinisherSound = "minecraft:entity.player.death";
        }

        if (!DATA.playFinishers) {
            if (!FINISHER_ANIMATION_OFF.equals(DATA.finisherAnimation)) {
                DATA.lastFinisherAnimation = DATA.finisherAnimation;
            }
            if (!FINISHER_SOUND_OFF.equals(DATA.finisherSound)) {
                DATA.lastFinisherSound = DATA.finisherSound;
            }
            DATA.finisherAnimation = FINISHER_ANIMATION_OFF;
            DATA.finisherSound = FINISHER_SOUND_OFF;
        }

        save();
    }

    public static List<String> availableSoundIds() {
        List<String> ids = new ArrayList<>();
        ids.add(FINISHER_SOUND_OFF);
        ids.add(FINISHER_SOUND_MATCH);
        ids.add(FINISHER_SOUND_CUSTOM);
        ids.add(DefaultKneelFallAnimation.ID);
        ids.add("lightning");
        ids.add("frozen");
        return ids;
    }

    public static void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Finishers config");
            lines.add("play_finishers = " + DATA.playFinishers);
            lines.add("finisher_animation = \"" + DATA.finisherAnimation + "\"");
            lines.add("finisher_sound = \"" + DATA.finisherSound + "\"");
            lines.add("custom_finisher_sound = \"" + DATA.customFinisherSound + "\"");
            lines.add("last_finisher_animation = \"" + DATA.lastFinisherAnimation + "\"");
            lines.add("last_finisher_sound = \"" + DATA.lastFinisherSound + "\"");
            Files.write(path, lines);
        } catch (IOException ignored) {
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static class FinishersConfigData {
        public boolean playFinishers = true;
        public String finisherAnimation = DefaultKneelFallAnimation.ID;
        public String finisherSound = FINISHER_SOUND_MATCH;
        public String customFinisherSound = "minecraft:entity.player.death";
        public String lastFinisherAnimation = DefaultKneelFallAnimation.ID;
        public String lastFinisherSound = FINISHER_SOUND_MATCH;

        public void setPlayFinishers(boolean enabled) {
            playFinishers = enabled;
            if (!enabled) {
                if (!FINISHER_ANIMATION_OFF.equals(finisherAnimation)) {
                    lastFinisherAnimation = finisherAnimation;
                }
                if (!FINISHER_SOUND_OFF.equals(finisherSound)) {
                    lastFinisherSound = finisherSound;
                }
                finisherAnimation = FINISHER_ANIMATION_OFF;
                finisherSound = FINISHER_SOUND_OFF;
                return;
            }

            finisherAnimation = FINISHER_ANIMATION_OFF.equals(lastFinisherAnimation)
                ? DefaultKneelFallAnimation.ID
                : lastFinisherAnimation;
            finisherSound = FINISHER_SOUND_OFF.equals(lastFinisherSound)
                ? FINISHER_SOUND_MATCH
                : lastFinisherSound;
        }
    }
}
