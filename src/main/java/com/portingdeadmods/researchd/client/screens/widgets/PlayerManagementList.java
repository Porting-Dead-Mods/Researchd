package com.portingdeadmods.researchd.client.screens.widgets;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlayerManagementList extends ContainerObjectSelectionList<PlayerManagementList.Entry> {
    public static final ResourceLocation PLAYER_ENTRY_TEXTURE = Researchd.rl("player");
    private int scrollBarPos;
    private final ClientResearchTeamHelper researchTeamHelper;
    private AbstractWidget parent;

    public PlayerManagementList(int width, int height, int y, int itemHeight, AbstractWidget parent) {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
        this.parent = parent;
        this.researchTeamHelper = new ClientResearchTeamHelper();
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        this.scrollBarPos = x + width;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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
    public boolean removeEntry(Entry entry) {
        return super.removeEntry(entry);
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
        if (this.visible) {
            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 500);
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                Entry entry = this.getEntryAtPosition(mouseX, mouseY);
                if (entry != null && entry.parent.visible) {
                    poseStack.translate(0, 0, 502);
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, ClientResearchTeamHelper.getPlayerRole(entry.memberProfile.getId()).getDisplayName(), mouseX, mouseY);
                }
            }
            poseStack.popPose();
        }
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final GameProfile memberProfile;
        private final Consumer<Entry> refreshFunction;
        private final List<DraggableWidgetImageButton> buttonWidgets;

        private final AbstractWidget parent;

        public Entry(GameProfile memberProfile, PlayerManagementDraggableWidget.PlayerManagementButtons buttonSettings, AbstractWidget parent, Consumer<Entry> refreshFunction) {
            this.memberProfile = memberProfile;
            this.refreshFunction = refreshFunction;
            this.buttonWidgets = new ArrayList<>();
            this.parent = parent;

            if (ClientResearchTeamHelper.getPlayerRole(memberProfile.getId()) != ResearchTeamRole.OWNER) {
                for (Map.Entry<PlayerManagementDraggableWidget.PlayerManagementButtonType, WidgetSprites> entry : buttonSettings.getSprites().entrySet()) {
                    this.buttonWidgets.add(new DraggableWidgetImageButton(0, 0, 12, 12, entry.getValue(), btn -> {
                        switch (entry.getKey()) {
                            case PROMOTE -> ClientResearchTeamHelper.promoteTeamMemberSynced(this.memberProfile);
                            case DEMOTE -> ClientResearchTeamHelper.demoteTeamMemberSynced(this.memberProfile);
                            case REMOVE -> ClientResearchTeamHelper.removeTeamMemberSynced(this.memberProfile);
                        }
                        Entry.this.refreshFunction.accept(Entry.this);
                    }));
                }
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
