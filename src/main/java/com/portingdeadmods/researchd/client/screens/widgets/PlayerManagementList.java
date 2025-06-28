package com.portingdeadmods.researchd.client.screens.widgets;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerManagementList extends ContainerObjectSelectionList<PlayerManagementList.Entry> {
    public static final ResourceLocation PLAYER_ENTRY_TEXTURE = Researchd.rl("player");
    private int scrollBarPos;

    public PlayerManagementList(int width, int height, int y, int itemHeight) {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        this.scrollBarPos = x + width;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    protected int getScrollbarPosition() {
        return this.scrollBarPos;
    }

    @Override
    public int addEntry(Entry entry) {
        return super.addEntry(entry);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Entry entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry != null) {
            entry.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 500);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
        poseStack.popPose();
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final GameProfile memberProfile;
        private final PlayerManagementDraggableWidget.PlayerManagementButtons buttonSettings;
        private final List<DraggableWidgetImageButton> buttonWidgets;

        private final AbstractWidget parent;

        public Entry(GameProfile memberProfile, PlayerManagementDraggableWidget.PlayerManagementButtons buttonSettings, AbstractWidget parent) {
            this.memberProfile = memberProfile;
            this.buttonSettings = buttonSettings;
            this.buttonWidgets = new ArrayList<>();
            this.parent = parent;

            for (WidgetSprites sprites : this.buttonSettings.getSprites()) {
                this.buttonWidgets.add(new DraggableWidgetImageButton(0, 0, 12, 12, sprites, btn -> {
                    Researchd.LOGGER.debug("HEllo :3");
                }));
            }
        }

        public List<DraggableWidgetImageButton> getButtonWidgets() {
            return buttonWidgets;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            if (!this.parent.visible) return;

            PoseStack poseStack = guiGraphics.pose();
            guiGraphics.blitSprite(PLAYER_ENTRY_TEXTURE, left + 66, top - 4, 84, 16);
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 1);
                PlayerFaceRenderer.draw(guiGraphics, Minecraft.getInstance().player.getSkin(), left + 66 + 3, top - 4 + 3, 10);
            }
            poseStack.popPose();
            guiGraphics.drawString(Minecraft.getInstance().font, this.memberProfile.getName(), left + 66 + 3 + 12, top - 4 + 4, -1);
            int i = 0;
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 1);
                for (DraggableWidgetImageButton widget : this.buttonWidgets) {
                    widget.setPosition(left + 66 + 84 - (i + 1) * (12 + 2), top - 4 + 2);
                    widget.render(guiGraphics, mouseX, mouseY, partialTicks);
                    i++;
                }
            }
            poseStack.popPose();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (DraggableWidgetImageButton widget : this.buttonWidgets) {
                if (widget.isHovered()) {
                    widget.mouseClicked(mouseX, mouseY, button);
                    break;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }
}
