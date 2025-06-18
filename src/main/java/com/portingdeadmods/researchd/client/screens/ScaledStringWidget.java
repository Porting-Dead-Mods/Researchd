package com.portingdeadmods.researchd.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class ScaledStringWidget extends AbstractStringWidget {
    private final float scale;
    private float alignX;

    public ScaledStringWidget(Component message, Font font, float scale) {
        this(0, 0, (int) (font.width(message.getVisualOrderText()) * scale), (int) (font.lineHeight * scale), message, font, scale);
    }

    public ScaledStringWidget(int width, int height, Component message, Font font, float scale) {
        this(0, 0, width, height, message, font, scale);
    }

    public ScaledStringWidget(int x, int y, int width, int height, Component message, Font font, float scale) {
        super(x, y, width, height, message, font);
        this.alignX = 0.5F;
        this.active = false;
        this.scale = scale;
    }

    public ScaledStringWidget setColor(int color) {
        super.setColor(color);
        return this;
    }

    private ScaledStringWidget horizontalAlignment(float horizontalAlignment) {
        this.alignX = horizontalAlignment;
        return this;
    }

    public ScaledStringWidget alignLeft() {
        return this.horizontalAlignment(0.0F);
    }

    public ScaledStringWidget alignCenter() {
        return this.horizontalAlignment(0.5F);
    }

    public ScaledStringWidget alignRight() {
        return this.horizontalAlignment(1.0F);
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component component = this.getMessage();
        Font font = this.getFont();
        int i = this.getWidth();
        int j = font.width(component);
        int k = this.getX() + Math.round(this.alignX * (float)(i - j));
        int l = this.getY() + (this.getHeight() - 9) / 2;
        FormattedCharSequence formattedcharsequence = j > i ? this.clipText(component, i) : component.getVisualOrderText();
        drawString(guiGraphics, font, formattedcharsequence, k, l, this.getColor(), this.scale);
    }

    protected void drawString(GuiGraphics guiGraphics, Font font, FormattedCharSequence component, int x, int y, int color, float scale) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        {
            poseStack.scale(scale, scale, scale);
            guiGraphics.drawString(font, component, (int) (x / scale), (int) (y / scale), color);
        }
        poseStack.popPose();
    }

    private FormattedCharSequence clipText(Component message, int width) {
        Font font = this.getFont();
        FormattedText formattedtext = font.substrByWidth(message, (int) (width - font.width(CommonComponents.ELLIPSIS) * this.scale));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS));
    }

}
