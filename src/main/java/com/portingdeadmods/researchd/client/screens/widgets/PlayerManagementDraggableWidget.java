package com.portingdeadmods.researchd.client.screens.widgets;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerManagementDraggableWidget extends AbstractDraggableWidget {
    public static final ResourceLocation WINDOW_TEXTURE = Researchd.rl("textures/gui/player_management_window.png");
    private final List<GameProfile> members;
    private final PlayerManagementButtons buttonSettings;
    private final List<DraggableWidgetImageButton> buttonWidgets;
    private final PlayerManagementList managementList;

    public PlayerManagementDraggableWidget(int x, int y, List<GameProfile> members, PlayerManagementButtons buttonSettings, Component message) {
        super(x, y, 128, 128, message);
        this.members = members;
        this.buttonSettings = buttonSettings;
        this.buttonWidgets = new ArrayList<>();
        int i = 0;
        for (WidgetSprites sprites : this.buttonSettings.getSprites()) {
            this.buttonWidgets.add(new DraggableWidgetImageButton(getX() + 6 + i * (12 + 2), getY() + 6, 12, 12, sprites, btn -> {}));
            i++;
        }
        this.managementList = new PlayerManagementList(84, 116, 0, 16);
        this.managementList.setPosition(x + 6, y + 6);
        for (GameProfile member : members) {
            this.managementList.addEntry(new PlayerManagementList.Entry(member, buttonSettings));
        }
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        consumer.accept(this.managementList);
    }

    @Override
    protected void onMoved() {
        super.onMoved();

        int i = 0;
        for (DraggableWidgetImageButton button : this.buttonWidgets) {
            button.setPosition(getX() + 6 + i * (12 + 2), getY() + 6);
            i++;
        }
        this.managementList.setPosition(getX() + 6, getY() + 6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.managementList.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.managementList.setScrollAmount(this.managementList.getScrollAmount() - scrollY * (double)16 / (double)2.0F);
        return true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        super.renderWidget(guiGraphics, mouseX, mouseY, v);

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 500);
            GuiUtils.drawImg(guiGraphics, WINDOW_TEXTURE, getX(), getY(), getWidth(), getHeight());
        }
        poseStack.popPose();
    }

    public record PlayerManagementButtons(boolean removeMembers, boolean promoteMembers, boolean demoteMembers, boolean transferOwnership) {
        public static final WidgetSprites REMOVE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("remove_member"), Researchd.rl("remove_member_focused"));
        public static final WidgetSprites PROMOTE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("promote_member"), Researchd.rl("promote_member_focused"));
        public static final WidgetSprites DEMOTE_MEMBERS_SPRITES = new WidgetSprites(Researchd.rl("demote_member"), Researchd.rl("demote_member_focused"));
        public static final WidgetSprites TRANSFER_OWNERSHIP_SPRITES = new WidgetSprites(Researchd.rl("transfer_ownership"), Researchd.rl("transfer_ownership_focused"));

        public List<WidgetSprites> getSprites() {
            List<WidgetSprites> sprites = new ArrayList<>(4);
            if (this.removeMembers()) {
                sprites.add(REMOVE_MEMBERS_SPRITES);
            }
            if (this.demoteMembers()) {
                sprites.add(DEMOTE_MEMBERS_SPRITES);
            }
            if (this.promoteMembers()) {
                sprites.add(PROMOTE_MEMBERS_SPRITES);
            }
            if (this.transferOwnership()) {
                sprites.add(TRANSFER_OWNERSHIP_SPRITES);
            }
            return sprites;
        }

    }

}
