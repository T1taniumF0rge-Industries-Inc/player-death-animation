// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class PDADeathTransitionScreen extends Screen {
    private static final int FLASH_MS = 250;
    private static final int FADE_MS = 3000;

    private final Text deathMessage;
    private final boolean hardcore;
    private DeathScreen deathScreen;
    private long startedAt;
    private boolean switchedToDeathScreen;

    protected PDADeathTransitionScreen(Text deathMessage, boolean hardcore) {
        super(Text.empty());
        this.deathMessage = deathMessage;
        this.hardcore = hardcore;
    }

    @Override
    protected void init() {
        super.init();
        this.startedAt = System.currentTimeMillis();
        this.switchedToDeathScreen = false;
        this.deathScreen = new DeathScreen(this.deathMessage, this.hardcore);
        if (this.client != null) {
            this.deathScreen.init(this.client, this.width, this.height);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long elapsed = System.currentTimeMillis() - startedAt;
        if (this.deathScreen != null) {
            this.deathScreen.render(context, mouseX, mouseY, delta);
        }

        float alpha;
        if (elapsed <= FLASH_MS) {
            alpha = 1.0f;
        } else {
            float fadeProgress = MathHelper.clamp((elapsed - FLASH_MS) / (float) FADE_MS, 0.0f, 1.0f);
            alpha = 1.0f - fadeProgress;
        }

        int alphaByte = (int) (alpha * 255.0f);
        int color = (alphaByte << 24);
        context.fill(0, 0, this.width, this.height, color);

        if (!this.switchedToDeathScreen && elapsed >= FLASH_MS + FADE_MS && client != null && this.deathScreen != null) {
            this.switchedToDeathScreen = true;
            client.setScreen(this.deathScreen);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
