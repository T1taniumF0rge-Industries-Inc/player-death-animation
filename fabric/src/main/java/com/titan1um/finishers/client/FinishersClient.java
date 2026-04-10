// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.client;

import com.titan1um.finishers.FinishersMod;
import com.titan1um.finishers.animation.DeathAnimationRegistry;
import com.titan1um.finishers.config.FinishersConfig;
import com.titan1um.finishers.config.FinishersConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FinishersClient implements ClientModInitializer {
    private static final Map<UUID, Integer> FINISHER_DEATH_TICKS = new HashMap<>();
    private static final Set<UUID> TRIGGERED_EFFECTS = new HashSet<>();

    @Override
    public void onInitializeClient() {
        FinishersConfig.load();
        DeathAnimationRegistry.bootstrap();
        registerCommands();
        registerTickFlow();
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

    private static void registerTickFlow() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            updateFinisherDeathTicks(client);
            triggerFinisherEffects(client);
        });
    }

    private static void updateFinisherDeathTicks(MinecraftClient client) {
        if (client.world == null) {
            FINISHER_DEATH_TICKS.clear();
            TRIGGERED_EFFECTS.clear();
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
        TRIGGERED_EFFECTS.retainAll(stillDead);
    }

    private static void triggerFinisherEffects(MinecraftClient client) {
        if (client.world == null || !FinishersConfig.get().playFinishers) {
            return;
        }

        String animation = FinishersConfig.get().finisherAnimation;
        if (FinishersConfig.FINISHER_ANIMATION_OFF.equals(animation)) {
            return;
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player.isAlive()) {
                continue;
            }

            UUID uuid = player.getUuid();
            int deathTicks = FINISHER_DEATH_TICKS.getOrDefault(uuid, 0);
            if (deathTicks < 10 || TRIGGERED_EFFECTS.contains(uuid)) {
                continue;
            }

            TRIGGERED_EFFECTS.add(uuid);
            playConfiguredSound(client, player, animation);
            spawnFinisherEffect(client, player, animation);
        }
    }

    private static void playConfiguredSound(MinecraftClient client, AbstractClientPlayerEntity player, String animation) {
        String soundMode = FinishersConfig.get().finisherSound;
        if (FinishersConfig.FINISHER_SOUND_OFF.equals(soundMode)) {
            return;
        }

        SoundEvent sound;
        if (FinishersConfig.FINISHER_SOUND_MATCH.equals(soundMode)) {
            sound = soundForAnimation(animation);
        } else if (FinishersConfig.FINISHER_SOUND_CUSTOM.equals(soundMode)) {
            Identifier id = Identifier.tryParse(FinishersConfig.get().customFinisherSound);
            sound = id == null ? null : Registries.SOUND_EVENT.get(id);
            if (sound == null) {
                sound = SoundEvents.ENTITY_PLAYER_DEATH;
            }
        } else {
            sound = soundForAnimation(soundMode);
        }

        client.world.playSound(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
    }

    private static SoundEvent soundForAnimation(String animation) {
        return switch (animation) {
            case "lightning" -> SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT;
            case "frozen" -> Registries.SOUND_EVENT.get(new Identifier(FinishersMod.MOD_ID, "frozen"));
            case "knee_fall" -> SoundEvents.ENTITY_WITHER_DEATH;
            default -> SoundEvents.ENTITY_PLAYER_DEATH;
        };
    }

    private static void spawnFinisherEffect(MinecraftClient client, AbstractClientPlayerEntity player, String animation) {
        switch (animation) {
            case "lightning" -> spawnLightningEffect(client, player);
            case "frozen" -> spawnFrozenEffect(client, player);
            default -> {
            }
        }
    }

    private static void spawnLightningEffect(MinecraftClient client, AbstractClientPlayerEntity player) {
        for (int i = 0; i < 6; i++) {
            double ox = (client.world.random.nextDouble() - 0.5) * 1.3;
            double oz = (client.world.random.nextDouble() - 0.5) * 1.3;
            client.world.addParticle(ParticleTypes.FLASH, player.getX() + ox, player.getBodyY(0.9), player.getZ() + oz, 0, 0, 0);
            client.world.addParticle(ParticleTypes.ELECTRIC_SPARK, player.getX() + ox, player.getBodyY(1.2), player.getZ() + oz, 0, 0.05, 0);
        }
    }

    private static void spawnFrozenEffect(MinecraftClient client, AbstractClientPlayerEntity player) {
        Box box = player.getBoundingBox().expand(0.2, 0.25, 0.2);
        for (int i = 0; i < 80; i++) {
            double x = box.minX + client.world.random.nextDouble() * (box.maxX - box.minX);
            double y = box.minY + client.world.random.nextDouble() * (box.maxY - box.minY);
            double z = box.minZ + client.world.random.nextDouble() * (box.maxZ - box.minZ);
            client.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, net.minecraft.block.Blocks.ICE.getDefaultState()), x, y, z, 0, 0.01, 0);
            client.world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0.01, 0);
        }

        BlockPos base = player.getBlockPos();
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (Math.abs(x) == 1 || Math.abs(z) == 1 || y == 2) {
                        client.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, net.minecraft.block.Blocks.PACKED_ICE.getDefaultState()),
                            base.getX() + 0.5 + x * 0.5,
                            base.getY() + y * 0.6,
                            base.getZ() + 0.5 + z * 0.5,
                            0,
                            0,
                            0);
                    }
                }
            }
        }
    }

    public static float getFinisherDeathTicks(AbstractClientPlayerEntity player, float partialTick) {
        return FINISHER_DEATH_TICKS.getOrDefault(player.getUuid(), 0) + partialTick;
    }

    public static boolean shouldRenderOwnFinisher() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return false;
        }

        Perspective perspective = client.options.getPerspective();
        return perspective != Perspective.FIRST_PERSON;
    }
}
