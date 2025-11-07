package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.team.widgets.AbstractDraggableWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreatePackPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/pack_creation_popup");

    public CreatePackPopupWidget() {
        super(0, 0, 160, 176, CommonComponents.EMPTY);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(SPRITE, this.getX(), this.getY(), this.width, this.height);
    }

}
