// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.mixin;

import com.playerdeathanimation.animation.DeathAnimationRegistry;
import com.playerdeathanimation.config.PDAConfig;
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
    private void pda$applyDeathAnimation(LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity player)) {
            return;
        }

        if (player == net.minecraft.client.MinecraftClient.getInstance().player) {
            return;
        }

        if (player.isAlive() || !PDAConfig.get().enableObserverAnimation) {
            return;
        }

        int deathTicks = ((LivingEntityAccessor) player).pda$getDeathTime();
        DeathAnimationRegistry.apply((PlayerEntityModel<AbstractClientPlayerEntity>) (Object) this, player, deathTicks + animationProgress);
    }
}
