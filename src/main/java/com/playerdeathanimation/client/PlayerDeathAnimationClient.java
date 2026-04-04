// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.client;

import com.playerdeathanimation.animation.DeathAnimationRegistry;
import com.playerdeathanimation.config.PDAConfig;
import com.playerdeathanimation.config.PDAConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;

public class PlayerDeathAnimationClient implements ClientModInitializer {
    private static boolean animationTriggered = false;

    @Override
    public void onInitializeClient() {
        PDAConfig.load();
        DeathAnimationRegistry.bootstrap();
        registerCommands();
        registerDeathScreenFlow();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("pda")
                .executes(context -> openConfigScreen()));

            dispatcher.register(ClientCommandManager.literal("playerdeathanimation")
                .executes(context -> openConfigScreen()));
        });
    }

    private static int openConfigScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.send(() -> client.setScreen(new PDAConfigScreen(client.currentScreen)));
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

            if (animationTriggered || !PDAConfig.get().enableSelfDeathEffect) {
                return;
            }

            if (client.currentScreen instanceof DeathScreen || client.currentScreen instanceof PDADeathTransitionScreen) {
                animationTriggered = true;
                return;
            }

            animationTriggered = true;
            client.setScreen(new PDADeathTransitionScreen(client.player.getDamageTracker().getDeathMessage(), client.player.getWorld().getLevelProperties().isHardcore()));
        });
    }
}
