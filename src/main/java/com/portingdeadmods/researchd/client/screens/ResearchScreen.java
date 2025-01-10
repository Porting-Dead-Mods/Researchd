package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResearchScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_screen.png");

    public ResearchScreen() {
        super(Component.translatable("screen.researchd.research"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, 256, 256, 256, 256);
    }
}
