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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FinishersClient implements ClientModInitializer {
    private static boolean animationTriggered = false;
    private static boolean bypassDeathScreenInterception = false;
    private static final Map<UUID, Integer> FINISHER_DEATH_TICKS = new HashMap<>();

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
            updateFinisherDeathTicks(client);

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
            blackScreenDeathEffect(client, null);
        });
    }

    private static void updateFinisherDeathTicks(MinecraftClient client) {
        if (client.world == null) {
            FINISHER_DEATH_TICKS.clear();
            return;
        }

        Set<UUID> stillDead = new HashSet<>();
        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (!player.isAlive()) {
                UUID uuid = player.getUuid();
                stillDead.add(uuid);
                FINISHER_DEATH_TICKS.merge(uuid, 1, Integer::sum);
            }
        }

        FINISHER_DEATH_TICKS.keySet().retainAll(stillDead);
    }

    public static float getFinisherDeathTicks(AbstractClientPlayerEntity player, float partialTick) {
        return FINISHER_DEATH_TICKS.getOrDefault(player.getUuid(), 0) + partialTick;
    }

    public static void blackScreenDeathEffect(MinecraftClient client, DeathScreen deathScreen) {
        if (client == null || client.player == null || client.world == null) {
            return;
        }

        if (!FinishersConfig.get().enableSelfBlackScreenDeathEffect) {
            return;
        }

        if (client.currentScreen instanceof FinishersDeathTransitionScreen) {
            return;
        }

        if (deathScreen != null) {
            client.setScreen(new FinishersDeathTransitionScreen(deathScreen));
            return;
        }

        client.setScreen(new FinishersDeathTransitionScreen(
            client.player.getDamageTracker().getDeathMessage(),
            client.player.getWorld().getLevelProperties().isHardcore()));
    }

    public static void setScreenWithoutDeathIntercept(MinecraftClient client, Screen screen) {
        bypassDeathScreenInterception = true;
        try {
            client.setScreen(screen);
        } finally {
            bypassDeathScreenInterception = false;
        }
    }

    public static boolean isBypassingDeathScreenInterception() {
        return bypassDeathScreenInterception;
    }
}
