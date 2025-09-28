package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.client.cache.AllPlayersCache;
import com.portingdeadmods.researchd.client.screens.ContainerWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PlayerManagementList extends ContainerWidget<PlayerManagementList.Entry> {
    public static final ResourceLocation PLAYER_ENTRY_TEXTURE = Researchd.rl("player");
    private final Map<Entry, List<DraggableWidgetImageButton>> buttonWidgets;
    private final AbstractWidget parent;

	private boolean _should_add_button(Entry item, PlayerManagementDraggableWidget.PlayerManagementButtonType type) {
		ResearchTeamRole clientRole = ClientResearchTeamHelper.getRole();
		ResearchTeamRole targetRole = item.teamMember.role();

		return switch (type) {
			case PROMOTE -> clientRole == ResearchTeamRole.OWNER && targetRole == ResearchTeamRole.MEMBER;
			case DEMOTE -> clientRole == ResearchTeamRole.OWNER && targetRole == ResearchTeamRole.MODERATOR;
			case REMOVE -> clientRole == ResearchTeamRole.OWNER && targetRole != ResearchTeamRole.OWNER
					|| clientRole == ResearchTeamRole.MODERATOR && targetRole == ResearchTeamRole.MEMBER;
			case INVITE_PLAYER -> clientRole == ResearchTeamRole.OWNER || clientRole == ResearchTeamRole.MODERATOR;
			case TRANSFER_OWNERSHIP -> clientRole == ResearchTeamRole.OWNER && targetRole != ResearchTeamRole.OWNER;
		};
	}

    public PlayerManagementList(int width, int height, int itemWidth, int itemHeight, Collection<PlayerManagementList.Entry> items, boolean renderScroller, AbstractWidget parent) {
        super(width, height, itemWidth, itemHeight, items, renderScroller);
        this.buttonWidgets = new HashMap<>();
        this.parent = parent;
        for (Entry item : items) {
			buttonWidgets.put(item, new ArrayList<>());

            if (item.teamMember.role() != ResearchTeamRole.OWNER) {
                for (Map.Entry<PlayerManagementDraggableWidget.PlayerManagementButtonType, WidgetSprites> entry : item.buttonSettings().getSprites().entrySet()) {
					if (!this._should_add_button(item, entry.getKey())) continue;
                    this.buttonWidgets.get(item).add(new DraggableWidgetImageButton(0, 0, 12, 12, entry.getValue(), btn -> {
                        switch (entry.getKey()) {
                            case PROMOTE -> ClientResearchTeamHelper.promoteTeamMemberSynced(item.teamMember());
                            case DEMOTE -> ClientResearchTeamHelper.demoteTeamMemberSynced(item.teamMember());
                            case REMOVE -> ClientResearchTeamHelper.removeTeamMemberSynced(item.teamMember());
                            case INVITE_PLAYER -> ClientResearchTeamHelper.sendTeamInviteSynced(item.teamMember());
                            case TRANSFER_OWNERSHIP -> {
                                if (this.parent instanceof PlayerManagementDraggableWidget widget) {
                                    widget.openPopupWidget(item.teamMember());
                                }
                            }
                        }
						if (this.parent instanceof PlayerManagementDraggableWidget widget)
                            widget.refreshFunction.accept(item, entry.getKey());
                    }));
                }
            }
        }

		resort();
	}

    @Override
    public void clickedItem(PlayerManagementList.Entry item, int index, int left, int top, int mouseX, int mouseY) {
    }

    @Override
    public void renderItem(GuiGraphics guiGraphics, PlayerManagementList.Entry item, int index, int left, int top, int mouseX, int mouseY) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(0, 0, PlayerManagementDraggableWidget.BACKGROUND_Z + 1);
            guiGraphics.blitSprite(PLAYER_ENTRY_TEXTURE, left, top, 84, 16);
        }
        poseStack.popPose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, PlayerManagementDraggableWidget.BACKGROUND_Z + 2);
            PlayerFaceRenderer.draw(guiGraphics, AllPlayersCache.getSkin(item.teamMember.player()), left + 3, top + 3, 10);
            guiGraphics.drawScrollingString(Minecraft.getInstance().font, Component.literal(AllPlayersCache.getName(item.teamMember.player())).withStyle(ChatFormatting.WHITE), left + 3 + 12, left + 84 - this.buttonWidgets.get(item).size() * (12 + 2) - 2, top + 4, -1);
        }
        poseStack.popPose();

        int i = 0;
        poseStack.pushPose();
        {
            poseStack.translate(0, 0, PlayerManagementDraggableWidget.BACKGROUND_Z + 3);
            for (DraggableWidgetImageButton widget : this.buttonWidgets.get(item)) {
                widget.setPosition(left + 84 - (i + 1) * (12 + 2), top + 2);
                widget.render(guiGraphics, mouseX, mouseY, -1);
                i++;
            }
        }
        poseStack.popPose();
    }

	public void resort() {
		sortEntriesBy(Comparator.comparing(entry -> ClientResearchTeamHelper.getPlayerRole(entry.teamMember().player()).getPermissionLevel(), Comparator.reverseOrder()));
	}

    public record Entry(TeamMember teamMember, PlayerManagementDraggableWidget.PlayerManagementButtons buttonSettings) {
    }
}
