package com.portingdeadmods.researchd.client.screens;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class TeamMemberButton extends ImageButton {
    private GameProfile gameProfile;

    public TeamMemberButton(int width, int height, GameProfile gameProfile, WidgetSprites sprites, OnPress onPress) {
        this(0, 0, width, height, gameProfile, sprites, onPress);
    }
    public TeamMemberButton(int x, int y, int width, int height, GameProfile gameProfile, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
        this.gameProfile = gameProfile;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PlayerFaceRenderer.draw(guiGraphics, Minecraft.getInstance().player.getSkin(), getX() + 4, getY() + 4, 12);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(this.gameProfile.getName()), getX() + 4 + 12 + 2, getY() + 2, -1, true);
        //renderScrollingString(guiGraphics, Minecraft.getInstance().font, Component.literal(this.gameProfile.getName()), getX() + 4 + 12 + 1, getY() - 10, getX() + getWidth() - 2, getY() + getHeight(), -1);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Member"), getX() + 4 + 12 + 2, getY() + 12, (int) Mth.lerp(0.5, ChatFormatting.YELLOW.getColor(), ChatFormatting.GOLD.getColor()), true);
    }
}
