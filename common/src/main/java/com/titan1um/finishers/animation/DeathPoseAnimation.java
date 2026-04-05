// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.animation;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public interface DeathPoseAnimation {
    String id();

    void apply(PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float deathTicks);
}
