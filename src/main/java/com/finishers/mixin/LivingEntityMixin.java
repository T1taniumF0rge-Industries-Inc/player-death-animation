// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.mixin;

import com.titan1um.finishers.config.FinishersConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public int deathTime;

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void finishers$disableVanillaDeathSmoke(byte status, CallbackInfo ci) {
        if (status != EntityStatuses.ADD_DEATH_PARTICLES || !FinishersConfig.get().enableFinishers) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof AbstractClientPlayerEntity) {
            ci.cancel();
        }
    }
}
