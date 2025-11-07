package com.portingdeadmods.researchd.client.screens.research.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackgroundStringWidget extends StringWidget {
    private final ResourceLocation backgroundSprite;

    public BackgroundStringWidget(Component message, Font font, ResourceLocation backgroundSprite) {
        super(message, font);
        this.backgroundSprite = backgroundSprite;
    }

    public BackgroundStringWidget(int width, int height, Component message, Font font, ResourceLocation backgroundSprite) {
        super(width, height, message, font);
        this.backgroundSprite = backgroundSprite;
    }

    public BackgroundStringWidget(int x, int y, int width, int height, Component message, Font font, ResourceLocation backgroundSprite) {
        super(x, y, width, height, message, font);
        this.backgroundSprite = backgroundSprite;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(this.backgroundSprite, this.getX(), this.getY(), this.getWidth() + 28, this.getHeight());

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
