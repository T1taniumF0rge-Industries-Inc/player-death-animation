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

        this.addDrawableChild(ButtonWidget.builder(selfToggleText(), button -> {
                draft.enableSelfBlackScreenDeathEffect = !draft.enableSelfBlackScreenDeathEffect;
                button.setMessage(selfToggleText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(observerToggleText(), button -> {
                draft.enableFinishers = !draft.enableFinishers;
                button.setMessage(observerToggleText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(animationText(), button -> {
                cycleAnimation();
                button.setMessage(animationText());
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

    private Text selfToggleText() {
        return Text.translatable("screen.finishers.self_effect", stateText(draft.enableSelfBlackScreenDeathEffect));
    }

    private Text observerToggleText() {
        return Text.translatable("screen.finishers.observer_effect", stateText(draft.enableFinishers));
    }

    private Text animationText() {
        return Text.translatable("screen.finishers.animation", finisherDisplayName(draft.finisherType));
    }

    private Text stateText(boolean value) {
        return value ? Text.translatable("options.on") : Text.translatable("options.off");
    }

    private Text finisherDisplayName(String animationId) {
        return Text.translatable("screen.finishers.animation_option." + animationId);
    }

    private void cycleAnimation() {
        List<String> options = DeathAnimationRegistry.availableAnimationIds();
        int current = options.indexOf(draft.finisherType);
        if (current < 0) {
            draft.finisherType = options.get(0);
            return;
        }

        int next = (current + 1) % options.size();
        draft.finisherType = options.get(next);
    }

    private void apply() {
        FinishersConfig.get().enableSelfBlackScreenDeathEffect = draft.enableSelfBlackScreenDeathEffect;
        FinishersConfig.get().enableFinishers = draft.enableFinishers;
        FinishersConfig.get().finisherType = draft.finisherType;
        FinishersConfig.save();
    }

    private FinishersConfig.FinishersConfigData copy(FinishersConfig.FinishersConfigData source) {
        FinishersConfig.FinishersConfigData c = new FinishersConfig.FinishersConfigData();
        c.enableSelfBlackScreenDeathEffect = source.enableSelfBlackScreenDeathEffect;
        c.enableFinishers = source.enableFinishers;
        c.finisherType = source.finisherType;
        return c;
    }
}
