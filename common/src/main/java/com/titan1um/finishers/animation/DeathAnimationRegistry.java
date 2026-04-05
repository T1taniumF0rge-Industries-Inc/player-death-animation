// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.animation;

import com.titan1um.finishers.config.FinishersConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class DeathAnimationRegistry {
    public static final String RANDOM_ID = "random";

    private static final Map<String, DeathPoseAnimation> ANIMATIONS = new LinkedHashMap<>();

    private DeathAnimationRegistry() {
    }

    public static void bootstrap() {
        register(new DefaultKneelFallAnimation());
    }

    public static void register(DeathPoseAnimation animation) {
        ANIMATIONS.put(animation.id(), animation);
    }

    public static List<String> availableAnimationIds() {
        List<String> ids = new ArrayList<>();
        ids.add(DefaultKneelFallAnimation.ID);
        ids.add(RANDOM_ID);
        return ids;
    }

    public static DeathPoseAnimation getActiveAnimation() {
        String configuredId = FinishersConfig.get().finisherType;
        if (RANDOM_ID.equals(configuredId)) {
            return getRandomAnimation();
        }

        DeathPoseAnimation configured = ANIMATIONS.get(configuredId);
        if (configured != null) {
            return configured;
        }
        return ANIMATIONS.get(DefaultKneelFallAnimation.ID);
    }

    private static DeathPoseAnimation getRandomAnimation() {
        if (ANIMATIONS.isEmpty()) {
            return null;
        }

        List<DeathPoseAnimation> pool = new ArrayList<>(ANIMATIONS.values());
        int index = ThreadLocalRandom.current().nextInt(pool.size());
        return pool.get(index);
    }

    public static void apply(PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float deathTicks) {
        DeathPoseAnimation animation = getActiveAnimation();
        if (animation != null) {
            animation.apply(model, player, deathTicks);
        }
    }
}
