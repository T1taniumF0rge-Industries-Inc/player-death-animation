// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.mixin;

import com.titan1um.finishers.client.FinishersClient;
import com.titan1um.finishers.client.FinishersDeathTransitionScreen;
import com.titan1um.finishers.config.FinishersConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void finishers$replaceDeathScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof DeathScreen deathScreen)) {
            return;
        }

        if (FinishersClient.isBypassingDeathScreenInterception() || !FinishersConfig.get().enableSelfBlackScreenDeathEffect) {
            return;
        }

        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player == null || client.world == null || client.player.isAlive()) {
            return;
        }

        if (client.currentScreen instanceof FinishersDeathTransitionScreen) {
            ci.cancel();
            return;
        }

        FinishersClient.blackScreenDeathEffect(client, deathScreen);
        ci.cancel();
    }
}
