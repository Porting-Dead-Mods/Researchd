package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class WarningPopupWidget extends AbstractWidget {
    public static final ResourceLocation TEXTURE = Researchd.rl("textures/gui/popup_window.png");
    private Component title;
    private List<Component> bodyText;
    private final Button acceptButton;
    private final Button cancelButton;
    public GameProfile nextOwner;

    public WarningPopupWidget(int x, int y, Button.OnPress confirmButtonOnPress, Button.OnPress cancelButtonOnPress) {
        super(x, y, 160, 96, Component.empty());

        this.acceptButton = Button.builder(Component.literal("Ok"), confirmButtonOnPress)
                .bounds(x + width / 2, y + height / 2, 48, 16)
                .build();
        this.cancelButton = Button.builder(Component.literal("Cancel"), cancelButtonOnPress)
                .bounds(x + width / 2 + 48, y + height / 2, 48, 16)
                .build();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 1);

            GuiUtils.drawImg(guiGraphics, TEXTURE, getX(), getY(), width, height);

            Font font = Minecraft.getInstance().font;
            guiGraphics.drawCenteredString(font, this.title, getX() + width / 2, getY() + 4, -1);
            for (int i = 0; i < this.bodyText.size(); i++) {
                guiGraphics.drawCenteredString(font, this.bodyText.get(i), getX() + width / 2, getY() + 8 + ((i + 1) * font.lineHeight + 2), -1);
            }

            this.acceptButton.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.cancelButton.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.acceptButton.isHovered()) {
            this.acceptButton.mouseClicked(mouseX, mouseY, button);
        } else if (this.cancelButton.isHovered()) {
            this.cancelButton.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);

        this.acceptButton.setPosition(x + 24, y + height - 40);
        this.cancelButton.setPosition(x + width - 24 - this.cancelButton.getWidth(), y + height - 40);

    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public void setBodyText(List<Component> bodyText) {
        this.bodyText = bodyText;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

}
