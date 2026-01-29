package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class RegistryVerifyEditBox extends BackgroundEditBox {
    private final Registry<?> registry;

    public RegistryVerifyEditBox(Font font, WidgetSprites sprites, Registry<?> registry, int width, int height, Component message) {
        super(font, sprites, width, height, message);
        this.registry = registry;
    }

    public RegistryVerifyEditBox(Font font, Registry<?> registry, int width, int height, Component message) {
        super(font, width, height, message);
        this.registry = registry;
    }

    public Registry<?> getRegistry() {
        return registry;
    }

    @Override
    public void onValueChangedExtra(String newText) {
        ResourceLocation id = ResourceLocation.parse(newText);
        if (!this.registry.containsKey(id)) {
            this.setTextColor(FastColor.ARGB32.color(211, 47, 47));
        } else {
            this.setTextColor(14737632);
        }
    }

}
