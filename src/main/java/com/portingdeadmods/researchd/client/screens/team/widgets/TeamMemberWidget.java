package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TeamMemberWidget extends ImageButton {
    private final GameProfile gameProfile;
    private final ResearchTeamRole role;

    public TeamMemberWidget(int width, int height, GameProfile gameProfile, WidgetSprites sprites, OnPress onPress) {
        this(0, 0, width, height, gameProfile, sprites, onPress);
    }
    public TeamMemberWidget(int x, int y, int width, int height, GameProfile gameProfile, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
        this.gameProfile = gameProfile;
        this.role = ClientResearchTeamHelper.getPlayerRole(gameProfile.getId());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PlayerFaceRenderer.draw(guiGraphics, Minecraft.getInstance().player.getSkin(), getX() + 4, getY() + 4, 12);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(this.gameProfile.getName()), getX() + 4 + 12 + 2, getY() + 2, -1, true);
        //renderScrollingString(guiGraphics, Minecraft.getInstance().font, Component.literal(this.gameProfile.getName()), getX() + 4 + 12 + 1, getY() - 10, getX() + getWidth() - 2, getY() + getHeight(), -1);
        guiGraphics.drawString(Minecraft.getInstance().font, this.role.getDisplayName(), getX() + 4 + 12 + 2, getY() + 12, (int) Mth.lerp(0.5, ChatFormatting.YELLOW.getColor(), ChatFormatting.GOLD.getColor()), true);
    }
}
