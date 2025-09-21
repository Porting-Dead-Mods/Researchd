package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.authlib.GameProfile;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.ContainerWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PlayerManagementList extends ContainerWidget<PlayerManagementList.Entry> {
    public static final ResourceLocation PLAYER_ENTRY_TEXTURE = Researchd.rl("player");
    private final List<DraggableWidgetImageButton> buttonWidgets;
    private final AbstractWidget parent;

    public PlayerManagementList(int width, int height, int itemWidth, int itemHeight, Collection<PlayerManagementList.Entry> items, boolean renderScroller, AbstractWidget parent) {
        super(width, height, itemWidth, itemHeight, items, renderScroller);
        this.buttonWidgets = new ArrayList<>();
        this.parent = parent;

        for (Entry item : items) {
            if (ClientResearchTeamHelper.getPlayerRole(item.gameProfile().getId()) != ResearchTeamRole.OWNER) {
                for (Map.Entry<PlayerManagementDraggableWidget.PlayerManagementButtonType, WidgetSprites> entry : item.buttonSettings().getSprites().entrySet()) {
                    this.buttonWidgets.add(new DraggableWidgetImageButton(0, 0, 12, 12, entry.getValue(), btn -> {
                        switch (entry.getKey()) {
                            case PROMOTE -> ClientResearchTeamHelper.promoteTeamMemberSynced(item.gameProfile());
                            case DEMOTE -> ClientResearchTeamHelper.demoteTeamMemberSynced(item.gameProfile());
                            // FIXME: Gets called twice
                            case REMOVE -> ClientResearchTeamHelper.removeTeamMemberSynced(item.gameProfile());
                            case INVITE_PLAYER -> ClientResearchTeamHelper.sendTeamInviteSynced(item.gameProfile());
                            case TRANSFER_OWNERSHIP -> {
                                if (this.parent instanceof PlayerManagementDraggableWidget widget) {
                                    widget.openPopupWidget(item.gameProfile());
                                }
                            }
                        }
                        //PlayerManagementList.Entry.this.refreshFunction.accept(PlayerManagementList.Entry.this, entry.getKey());
                    }));
                }
            }
        }
    }

    @Override
    public void clickedItem(PlayerManagementList.Entry item, int index, int left, int top, int mouseX, int mouseY) {
    }

    @Override
    public void renderItem(GuiGraphics guiGraphics, PlayerManagementList.Entry item, int index, int left, int top, int mouseX, int mouseY) {
//        PoseStack poseStack = guiGraphics.pose();
//        guiGraphics.blitSprite(PLAYER_ENTRY_TEXTURE, left + 66, top - 4, 84, 16);
//        poseStack.pushPose();
//        {
//            poseStack.translate(0, 0, 100);
//            PlayerFaceRenderer.draw(guiGraphics, Minecraft.getInstance().player.getSkin(), left + 66 + 3, top - 4 + 3, 10);
//        }
//        poseStack.popPose();
//        guiGraphics.drawString(Minecraft.getInstance().font, item.gameProfile.getName(), left + 66 + 3 + 12, top - 4 + 4, -1);
//        int i = 0;
//        poseStack.pushPose();
//        {
//            poseStack.translate(0, 0, 1);
//            for (DraggableWidgetImageButton widget : this.buttonWidgets) {
//                widget.setPosition(left + 66 + 84 - (i + 1) * (12 + 2), top - 4 + 2);
//                widget.render(guiGraphics, mouseX, mouseY, -1);
//                i++;
//            }
//        }
//        poseStack.popPose();
            guiGraphics.drawString(Minecraft.getInstance().font, "Slay", 0, 0, -1);

    }

    public record Entry(GameProfile gameProfile, PlayerManagementDraggableWidget.PlayerManagementButtons buttonSettings) {
    }

}
