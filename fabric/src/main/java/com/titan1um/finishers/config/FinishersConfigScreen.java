// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.finishers.config;

import com.titan1um.finishers.animation.DeathAnimationRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class FinishersConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final FinishersConfig.FinishersConfigData draft;

    public FinishersConfigScreen(Screen parent) {
        super(Text.translatable("screen.finishers.title"));
        this.parent = parent;
        this.draft = copy(FinishersConfig.get());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4;

        this.addDrawableChild(ButtonWidget.builder(playFinishersText(), button -> {
                draft.setPlayFinishers(!draft.playFinishers);
                button.setMessage(playFinishersText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(animationText(), button -> {
                draft.finisherAnimation = nextIn(DeathAnimationRegistry.availableAnimationIds(), draft.finisherAnimation);
                if (!FinishersConfig.FINISHER_ANIMATION_OFF.equals(draft.finisherAnimation)) {
                    draft.lastFinisherAnimation = draft.finisherAnimation;
                }
                button.setMessage(animationText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(soundText(), button -> {
                draft.finisherSound = nextIn(FinishersConfig.availableSoundIds(), draft.finisherSound);
                if (!FinishersConfig.FINISHER_SOUND_OFF.equals(draft.finisherSound)) {
                    draft.lastFinisherSound = draft.finisherSound;
                }
                button.setMessage(soundText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(customSoundText(), button -> {
                draft.customFinisherSound = nextIn(List.of(
                    "minecraft:entity.player.death",
                    "minecraft:entity.wither.death",
                    "minecraft:entity.lightning_bolt.impact",
                    "finishers:frozen"
                ), draft.customFinisherSound);
                button.setMessage(customSoundText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        int bottomY = this.height - 28;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
                apply();
                close();
            }).dimensions(centerX - 102, bottomY, 100, BUTTON_HEIGHT)
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), button -> close())
            .dimensions(centerX + 2, bottomY, 100, BUTTON_HEIGHT)
            .build());
    }

    private String nextIn(List<String> options, String current) {
        int index = options.indexOf(current);
        if (index < 0) {
            return options.get(0);
        }
        return options.get((index + 1) % options.size());
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private Text playFinishersText() {
        return Text.translatable("screen.finishers.play_finishers", stateText(draft.playFinishers));
    }

    private Text animationText() {
        return Text.translatable("screen.finishers.animation", Text.translatable("screen.finishers.animation_option." + draft.finisherAnimation));
    }

    private Text soundText() {
        return Text.translatable("screen.finishers.sound", Text.translatable("screen.finishers.sound_option." + draft.finisherSound));
    }

    private Text customSoundText() {
        return Text.translatable("screen.finishers.custom_sound", Text.literal(draft.customFinisherSound));
    }

    private Text stateText(boolean value) {
        return value ? Text.translatable("options.on") : Text.translatable("options.off");
    }

    private void apply() {
        FinishersConfig.get().playFinishers = draft.playFinishers;
        FinishersConfig.get().finisherAnimation = draft.finisherAnimation;
        FinishersConfig.get().finisherSound = draft.finisherSound;
        FinishersConfig.get().customFinisherSound = draft.customFinisherSound;
        FinishersConfig.get().lastFinisherAnimation = draft.lastFinisherAnimation;
        FinishersConfig.get().lastFinisherSound = draft.lastFinisherSound;
        FinishersConfig.save();
    }

    private FinishersConfig.FinishersConfigData copy(FinishersConfig.FinishersConfigData source) {
        FinishersConfig.FinishersConfigData c = new FinishersConfig.FinishersConfigData();
        c.playFinishers = source.playFinishers;
        c.finisherAnimation = source.finisherAnimation;
        c.finisherSound = source.finisherSound;
        c.customFinisherSound = source.customFinisherSound;
        c.lastFinisherAnimation = source.lastFinisherAnimation;
        c.lastFinisherSound = source.lastFinisherSound;
        return c;
    }
}
