package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.client.screens.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class TeamMembersList extends ContainerWidget<TeamMember> {
    private final List<Component> playerNames;
    private final Map<UUID, PlayerSkin> playerSkins;

    public TeamMembersList(int width, int height, int itemWidth, int itemHeight, Collection<TeamMember> items, boolean renderScroller) {
        super(width, height, itemWidth, itemHeight, items, renderScroller);
        this.playerNames = new ArrayList<>();
        this.playerSkins = Collections.synchronizedMap(new HashMap<>());
        Minecraft mc = Minecraft.getInstance();
        for (TeamMember item : items) {
            Player player = mc.level.getPlayerByUUID(item.player());
            if (player != null) {
                Component name = player.getName();
                this.playerNames.add(name);
                GameProfile profile = new GameProfile(item.player(), name.getString());
                mc.getSkinManager().getOrLoad(profile).thenAccept(skin -> this.playerSkins.put(item.player(), skin));
            } else {
                this.playerNames.add(Component.literal("!Unknown Player!"));
            }
        }

    }

    @Override
    public void clickedItem(TeamMember item, int index, int left, int top, int mouseX, int mouseY) {

    }

    @Override
    public void renderItem(GuiGraphics guiGraphics, TeamMember item, int index, int left, int top, int mouseX, int mouseY) {
        ResourceLocation resourcelocation = ResearchTeamScreen.TEAM_MEMBER_BUTTON_SPRITES.get(this.isActive(), this.isItemHovered(index, mouseX, mouseY));
        guiGraphics.blitSprite(resourcelocation, left, top, this.getItemWidth(), this.getItemHeight());

        PlayerSkin skin = this.playerSkins.get(item.player());
        if (skin != null) {
            PlayerFaceRenderer.draw(guiGraphics, skin, left + 4, top + 4, 12);
        }
        //guiGraphics.drawString(Minecraft.getInstance().font, this.playerNames.get(index), left + 4 + 12 + 2, top + 2, -1, true);
        renderScrollingString(guiGraphics, Minecraft.getInstance().font, this.playerNames.get(index), left + 4 + 12 + 2, left + 4 + 12 + 2, top - 8, left + this.getItemWidth() - 1, top + this.getItemHeight(), -1);
        guiGraphics.drawString(Minecraft.getInstance().font, item.role().getDisplayName(), left + 4 + 12 + 2, top + 12, (int) Mth.lerp(0.5, ChatFormatting.YELLOW.getColor(), ChatFormatting.GOLD.getColor()));
    }

}
