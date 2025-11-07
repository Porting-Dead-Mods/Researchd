package com.portingdeadmods.researchd.client.screens.research.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

public class PDLImageButton extends ImageButton {
    public PDLImageButton(PDLButton.Builder<PDLImageButton> builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.sprites, builder.onPress, builder.message);
        this.setTooltip(builder.tooltip);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isHovered() && !this.getMessage().getString().isEmpty()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.getMessage(), mouseX, mouseY);
        }

    }

    public static PDLButton.Builder<PDLImageButton> builder(PDLButton.OnPress<PDLImageButton> onPress) {
        return PDLButton.builder(PDLImageButton::new, onPress);
    }
}
