package com.portingdeadmods.researchd.client.screens.team.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlayerManagementDraggableWidget extends AbstractDraggableWidget {
    public static final ResourceLocation WINDOW_TEXTURE = Researchd.rl("textures/gui/player_management_window.png");
    private final PlayerManagementButtons buttonSettings;
    private final List<DraggableWidgetImageButton> buttonWidgets;
    private final PlayerManagementList managementList;
    public final WarningPopupWidget popupWidget;

    public static final int BACKGROUND_Z = 500;

    public PlayerManagementDraggableWidget(int x, int y, List<TeamMember> members, PlayerManagementButtons buttonSettings, Component message) {
        super(x, y, 102, 128, message);
        this.buttonSettings = buttonSettings;
        this.buttonWidgets = new ArrayList<>();
        int i = 0;
        for (Map.Entry<PlayerManagementButtonType, WidgetSprites> entry : this.buttonSettings.getSprites().entrySet()) {
            this.buttonWidgets.add(new DraggableWidgetImageButton(getX() + 6 + i * (12 + 2), getY() + 6, 12, 12, entry.getValue(), btn -> {}));
            i++;
        }
        List<PlayerManagementList.Entry> entries = new ArrayList<>();
        for (TeamMember member : members) {
            entries.add(new PlayerManagementList.Entry(member, buttonSettings));
        }
        this.managementList = new PlayerManagementList(84, 118, 84, 16, entries, false, this);
        this.managementList.active = this.visible; // Probably redundant... but idrk some freaky stuff is happening with visibility
        this.managementList.setPosition(x + 5, y + 5);
        BiConsumer<PlayerManagementList.Entry, PlayerManagementButtonType> refreshFunction = (entry, type) -> {
            switch (type) {
                case REMOVE -> {
                    //this.managementList.removeEntry(entry);
                }
                case DEMOTE -> {
                }
                case PROMOTE -> {
                }
                case TRANSFER_OWNERSHIP -> {
                }
                case INVITE_PLAYER -> {
                }
            }
        };
        this.popupWidget = new WarningPopupWidget(0, 0, this::onOkPress, this::onCancelPress);
        this.popupWidget.visible = false;
    }

    public void openPopupWidget(TeamMember profile) {
        this.popupWidget.setTitle(Component.literal("Transfer Ownership"));
        this.popupWidget.setBodyText(List.of(
                Component.literal("Are you sure you"),
                Component.literal("want to transfer ownership"),
                Component.literal("to %s".formatted(PlayerUtils.getPlayerNameFromUUID(Minecraft.getInstance().level, profile.player())))
        ));
        this.popupWidget.visible = true;
        this.popupWidget.nextOwner = profile;
    }

    private void onCancelPress(Button button) {
        this.popupWidget.visible = false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.managementList.active = visible;
		this.managementList.visible = visible;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        consumer.accept(this.managementList);
        consumer.accept(this.popupWidget);
    }

    @Override
    protected void onMoved() {
        super.onMoved();

        int i = 0;
        for (DraggableWidgetImageButton button : this.buttonWidgets) {
            button.setPosition(getX() + 5 + i * (12 + 2), getY() + 5);
            i++;
        }
        this.managementList.setPosition(getX() + 5, getY() + 5);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.popupWidget.visible) {
            return this.popupWidget.mouseClicked(mouseX, mouseY, button);
        }
        return this.managementList.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        //this.managementList.setScrollAmount(this.managementList.getScrollAmount() - scrollY * (double)16 / (double)2.0F);
        return true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        super.renderWidget(guiGraphics, mouseX, mouseY, v);

        this.popupWidget.setPosition((guiGraphics.guiWidth() - this.popupWidget.getWidth()) / 2, (guiGraphics.guiHeight() - this.popupWidget.getHeight()) / 2);

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, BACKGROUND_Z);
            GuiUtils.drawImg(guiGraphics, WINDOW_TEXTURE, getX(), getY(), getWidth(), getHeight());
        }
        poseStack.popPose();

        this.managementList.render(guiGraphics, mouseX, mouseY, v);
    }

    private void onOkPress(Button btn) {
        this.popupWidget.visible = false;
        ClientResearchTeamHelper.transferOwnershipSynced(this.popupWidget.nextOwner);
        Minecraft.getInstance().setScreen(new ResearchTeamScreen());
    }

    public record PlayerManagementButtons(boolean removeMembers, boolean promoteMembers, boolean demoteMembers, boolean transferOwnership, boolean invitePlayer) {
        public static final WidgetSprites REMOVE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("remove_member"), Researchd.rl("remove_member_focused"));
        public static final WidgetSprites PROMOTE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("promote_member"), Researchd.rl("promote_member_focused"));
        public static final WidgetSprites DEMOTE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("demote_member"), Researchd.rl("demote_member_focused"));
        public static final WidgetSprites TRANSFER_OWNERSHIP_SPRITES = new WidgetSprites(Researchd.rl("transfer_ownership"), Researchd.rl("transfer_ownership_focused"));
        public static final WidgetSprites INVITE_PLAYER_SPRITES = new WidgetSprites(Researchd.rl("invite_button"), Researchd.rl("invite_button_focused"));

        public Map<PlayerManagementButtonType, WidgetSprites> getSprites() {
            Map<PlayerManagementButtonType, WidgetSprites> sprites = new LinkedHashMap<>(4);
            if (this.removeMembers()) {
                sprites.put(PlayerManagementButtonType.REMOVE, REMOVE_MEMBERS_SPRITES);
            }
            if (this.demoteMembers()) {
                sprites.put(PlayerManagementButtonType.DEMOTE, DEMOTE_MEMBERS_SPRITES);
            }
            if (this.promoteMembers()) {
                sprites.put(PlayerManagementButtonType.PROMOTE, PROMOTE_MEMBERS_SPRITES);
            }
            if (this.transferOwnership()) {
                sprites.put(PlayerManagementButtonType.TRANSFER_OWNERSHIP, TRANSFER_OWNERSHIP_SPRITES);
            }
            if (this.invitePlayer()) {
                sprites.put(PlayerManagementButtonType.INVITE_PLAYER, INVITE_PLAYER_SPRITES);
            }
            return sprites;
        }

    }

    public enum PlayerManagementButtonType {
        REMOVE,
        DEMOTE,
        PROMOTE,
        TRANSFER_OWNERSHIP,
        INVITE_PLAYER,
    }

}
