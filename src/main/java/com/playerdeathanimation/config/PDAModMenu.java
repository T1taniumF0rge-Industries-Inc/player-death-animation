// Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
package com.playerdeathanimation.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class PDAModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::create;
    }

    private Screen create(Screen parent) {
        return new PDAConfigScreen(parent);
    }
}
