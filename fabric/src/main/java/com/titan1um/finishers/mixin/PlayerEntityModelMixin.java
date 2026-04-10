// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.mixin;

import com.titan1um.finishers.animation.DeathAnimationRegistry;
import com.titan1um.finishers.client.FinishersClient;
import com.titan1um.finishers.config.FinishersConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin {
    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void finishers$applyDeathAnimation(LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity player)) {
            return;
        }

        if (player == net.minecraft.client.MinecraftClient.getInstance().player
            && !FinishersClient.shouldRenderOwnFinisher()) {
            return;
        }

        if (player.isAlive() || !FinishersConfig.get().playFinishers) {
            return;
        }

        ((LivingEntityAccessor) player).finishers$setDeathTime(0);
        float deathTicks = FinishersClient.getFinisherDeathTicks(player, animationProgress);
        DeathAnimationRegistry.apply((PlayerEntityModel<AbstractClientPlayerEntity>) (Object) this, player, deathTicks);
    }
}
