package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.Comparator;

public class TeamMembersList extends ContainerWidget<TeamMember> {
    public TeamMembersList(int width, int height, int itemWidth, int itemHeight, Collection<TeamMember> items, boolean renderScroller) {
        super(width, height, itemWidth, itemHeight, Orientation.VERTICAL, 1, 10, items, renderScroller);
        this.resort();
    }

    @Override
    public void clickedItem(TeamMember item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {

    }

    @Override
    protected int getScissorsHeight() {
        return this.getHeight();
    }

    @Override
    public void internalRenderItem(GuiGraphics guiGraphics, TeamMember item, int xIndex, int index, int left, int top, int mouseX, int mouseY) {
        ResourceLocation resourcelocation = ResearchTeamScreen.TEAM_MEMBER_BUTTON_SPRITES.get(this.isActive(), this.isItemHovered(index, mouseX, mouseY));
        guiGraphics.blitSprite(resourcelocation, left, top, this.getItemWidth(), this.getItemHeight());

        PlayerFaceRenderer.draw(guiGraphics, AllPlayersCache.getSkin(item.player()), left + 4, top + 4, 12);
        //guiGraphics.drawString(Minecraft.getInstance().font, this.playerNames.get(index), left + 4 + 12 + 2, top + 2, -1, true);
        renderScrollingString(guiGraphics, Minecraft.getInstance().font, Component.literal(AllPlayersCache.getName(item.player())).withStyle(ChatFormatting.WHITE), left + 4 + 12 + 2, left + 4 + 12 + 2, top - 8, left + this.getItemWidth() - 1, top + this.getItemHeight(), -1);
        guiGraphics.drawString(Minecraft.getInstance().font, item.role().getDisplayName(), left + 4 + 12 + 2, top + 12, (int) Mth.lerp(0.5, ChatFormatting.YELLOW.getColor(), ChatFormatting.GOLD.getColor()));
    }

    public void resort() {
        this.sortEntriesBy(Comparator.comparing(member -> member.role().getPermissionLevel(), Comparator.reverseOrder()));
    }
}
