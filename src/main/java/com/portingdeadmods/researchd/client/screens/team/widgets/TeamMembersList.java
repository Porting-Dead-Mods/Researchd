package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import java.util.Collection;
import java.util.Comparator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class TeamMembersList extends ContainerWidget<TeamMember> {
    public TeamMembersList(
            int width,
            int height,
            int itemWidth,
            int itemHeight,
            Collection<TeamMember> members,
            boolean renderScroller) {
        super(width, height, itemWidth, itemHeight, Orientation.VERTICAL, 1, 10, members, renderScroller);
        this.resort();
    }

    @Override
    public void clickedItem(TeamMember item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {}

    @Override
    protected int getScissorsHeight() {
        return this.getHeight();
    }

    @Override
    public void internalRenderItem(
            GuiGraphicsExtractor guiGraphics,
            TeamMember item,
            int xIndex,
            int index,
            int left,
            int top,
            int mouseX,
            int mouseY) {
        Identifier resourcelocation = ResearchTeamScreen.TEAM_MEMBER_BUTTON_SPRITES.get(
                this.isActive(), this.isItemHovered(index, mouseX, mouseY));
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, resourcelocation, left, top, this.getItemWidth(), this.getItemHeight());

        PlayerFaceExtractor.extractRenderState(
                guiGraphics, AllPlayersCache.getSkin(item.player()), left + 4, top + 4, 12);
        // guiGraphics.drawString(Minecraft.getInstance().font, this.playerNames.get(index), left + 4 + 12 + 2, top + 2,
        // -1, true);
        guiGraphics
                .textRenderer()
                .acceptScrolling(
                        Component.literal(AllPlayersCache.getName(item.player()))
                                .withStyle(ChatFormatting.WHITE),
                        left + 4 + 12 + 2,
                        left + 4 + 12 + 2,
                        left + this.getItemWidth() - 1,
                        top - 8,
                        top + this.getItemHeight());
        guiGraphics.text(
                Minecraft.getInstance().font,
                item.role().getDisplayName(),
                left + 4 + 12 + 2,
                top + 12,
                ARGB.opaque((int) Mth.lerp(0.5, ChatFormatting.YELLOW.getColor(), ChatFormatting.GOLD.getColor())));
    }

    public void resort() {
        this.sortEntriesBy(
                Comparator.comparing(member -> member.role().getPermissionLevel(), Comparator.reverseOrder()));
    }
}
