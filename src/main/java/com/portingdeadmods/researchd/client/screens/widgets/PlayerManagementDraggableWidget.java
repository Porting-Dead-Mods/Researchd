package com.portingdeadmods.researchd.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PlayerManagementDraggableWidget extends AbstractDraggableWidget {
    public static final ResourceLocation WINDOW_TEXTURE = Researchd.rl("textures/gui/player_management_window.png");
    public static final ResourceLocation PLAYER_ENTRY_TEXTURE = Researchd.rl("player");

    public PlayerManagementDraggableWidget(int x, int y, Component message) {
        super(x, y, 128, 128, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        super.renderWidget(guiGraphics, mouseX, mouseY, v);

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 500);
            GuiUtils.drawImg(guiGraphics, WINDOW_TEXTURE, getX(), getY(), getWidth(), getHeight());
            guiGraphics.enableScissor(getX() + 6, getY() + 6, getX() + 6 + getWidth() - 16 - 6 - 6, getY() + 6 + getHeight() - 6 - 6);
            {
                for (int i = 0; i < 5; i++) {
                    guiGraphics.blitSprite(PLAYER_ENTRY_TEXTURE, getX() + 6, getY() + 6 + i * 16, 84, 16);
                }
            }
            guiGraphics.disableScissor();
        }
        poseStack.popPose();
    }

}
