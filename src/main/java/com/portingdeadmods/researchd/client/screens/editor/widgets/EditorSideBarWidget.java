package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.lib.layout.AbstractLayoutWidget;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EditorSideBarWidget extends AbstractLayoutWidget<LinearLayout> {
    public static final ResourceLocation EDITOR_SIDE_BAR_TEXTURE = Researchd.rl("textures/gui/research_screen/editor_expandable.png");
    public static final WidgetSprites SETTINGS_BUTTON = new WidgetSprites(Researchd.rl("editor_open_settings"), Researchd.rl("editor_open_settings_highlighted"));

    public EditorSideBarWidget(int x, int y) {
        super(LinearLayout.vertical().spacing(2), x - 8, y + 8, 174, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 16, CommonComponents.EMPTY);
        this.layout.defaultCellSetting().paddingTop(3).paddingLeft(4);
        this.layout.addChild(PDLImageButton.builder(this::onSettingsButtonPressed)
                .size(14, 14)
                .sprites(SETTINGS_BUTTON)
                .tooltip(Tooltip.create(Component.literal("Configure Project Settings")))
                .build());
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    private void onSettingsButtonPressed(PDLImageButton button) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(0, 0, -1000);
            guiGraphics.blit(EDITOR_SIDE_BAR_TEXTURE, this.getX(), this.getY(), 0, 0, 0, 174, this.height, this.width, 16);
        }
        poseStack.popPose();

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

    }
}
