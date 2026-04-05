// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.client;

import com.titan1um.finishers.animation.DeathAnimationRegistry;
import com.titan1um.finishers.config.FinishersConfig;
import com.titan1um.finishers.config.FinishersConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;

public class FinishersClient implements ClientModInitializer {
    private static boolean animationTriggered = false;

    @Override
    public void onInitializeClient() {
        FinishersConfig.load();
        DeathAnimationRegistry.bootstrap();
        registerCommands();
        registerDeathScreenFlow();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("finishersConfig")
                .executes(context -> openConfigScreen())));
    }

    private static int openConfigScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.send(() -> client.setScreen(new FinishersConfigScreen(client.currentScreen)));
        }
        return 1;
    }

    private static void registerDeathScreenFlow() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                animationTriggered = false;
                return;
            }

            if (client.player.isAlive()) {
                animationTriggered = false;
                return;
            }

            if (animationTriggered || !FinishersConfig.get().enableSelfBlackScreenDeathEffect) {
                return;
            }

            if (client.currentScreen instanceof DeathScreen || client.currentScreen instanceof FinishersDeathTransitionScreen) {
                animationTriggered = true;
                return;
            }

            animationTriggered = true;
            client.setScreen(new FinishersDeathTransitionScreen(client.player.getDamageTracker().getDeathMessage(), client.player.getWorld().getLevelProperties().isHardcore()));
        });
    }
}
