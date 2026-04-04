// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.titan1um.playerdeathanimation.client;

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
    private long startedAt;

    protected PDADeathTransitionScreen(Text deathMessage, boolean hardcore) {
        super(Text.empty());
        this.deathMessage = deathMessage;
        this.hardcore = hardcore;
    }

    @Override
    protected void init() {
        super.init();
        this.startedAt = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long elapsed = System.currentTimeMillis() - startedAt;

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

        if (elapsed >= FLASH_MS + FADE_MS && client != null) {
            client.setScreen(new DeathScreen(this.deathMessage, this.hardcore));
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
