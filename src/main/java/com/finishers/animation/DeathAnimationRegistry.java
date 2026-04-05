// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.animation;

import com.titan1um.finishers.config.FinishersConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DeathAnimationRegistry {
    private static final Map<String, DeathPoseAnimation> ANIMATIONS = new LinkedHashMap<>();

    private DeathAnimationRegistry() {
    }

    public static void bootstrap() {
        register(new DefaultKneelFallAnimation());
    }

    public static void register(DeathPoseAnimation animation) {
        ANIMATIONS.put(animation.id(), animation);
    }

    public static DeathPoseAnimation getActiveAnimation() {
        DeathPoseAnimation configured = ANIMATIONS.get(FinishersConfig.get().finisherType);
        if (configured != null) {
            return configured;
        }
        return ANIMATIONS.get(DefaultKneelFallAnimation.ID);
    }

    public static void apply(PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float deathTicks) {
        DeathPoseAnimation animation = getActiveAnimation();
        if (animation != null) {
            animation.apply(model, player, deathTicks);
        }
    }
}
