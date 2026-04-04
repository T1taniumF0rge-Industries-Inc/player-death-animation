// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.playerdeathanimation.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PDAConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parent;
    private final PDAConfig.PDAConfigData draft;

    public PDAConfigScreen(Screen parent) {
        super(Text.translatable("screen.playerdeathanimation.title"));
        this.parent = parent;
        this.draft = copy(PDAConfig.get());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4;

        this.addDrawableChild(ButtonWidget.builder(selfToggleText(), button -> {
                draft.enableSelfDeathEffect = !draft.enableSelfDeathEffect;
                button.setMessage(selfToggleText());
            }).dimensions(centerX - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        y += 24;
        this.addDrawableChild(ButtonWidget.builder(observerToggleText(), button -> {
                draft.enableObserverAnimation = !draft.enableObserverAnimation;
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
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private Text selfToggleText() {
        return Text.translatable("screen.playerdeathanimation.self_effect", stateText(draft.enableSelfDeathEffect));
    }

    private Text observerToggleText() {
        return Text.translatable("screen.playerdeathanimation.observer_effect", stateText(draft.enableObserverAnimation));
    }

    private Text animationText() {
        return Text.translatable("screen.playerdeathanimation.animation", draft.observerAnimation);
    }

    private Text stateText(boolean value) {
        return value ? Text.translatable("options.on") : Text.translatable("options.off");
    }

    private void cycleAnimation() {
        draft.observerAnimation = "default_kneel_fall";
    }

    private void apply() {
        PDAConfig.get().enableSelfDeathEffect = draft.enableSelfDeathEffect;
        PDAConfig.get().enableObserverAnimation = draft.enableObserverAnimation;
        PDAConfig.get().observerAnimation = draft.observerAnimation;
        PDAConfig.save();
    }

    private PDAConfig.PDAConfigData copy(PDAConfig.PDAConfigData source) {
        PDAConfig.PDAConfigData c = new PDAConfig.PDAConfigData();
        c.enableSelfDeathEffect = source.enableSelfDeathEffect;
        c.enableObserverAnimation = source.enableObserverAnimation;
        c.observerAnimation = source.observerAnimation;
        return c;
    }
}
