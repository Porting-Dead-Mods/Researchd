package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.lib.widgets.AbstractLayoutWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PDLImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class EditorSideBarWidget extends AbstractLayoutWidget<LinearLayout> {
    public static final Identifier EDITOR_SIDE_BAR_TEXTURE =
            Researchd.rl("textures/gui/research_screen/editor_expandable.png");
    public static final WidgetSprites SETTINGS_BUTTON =
            new WidgetSprites(Researchd.rl("editor_open_settings"), Researchd.rl("editor_open_settings_highlighted"));

    public EditorSideBarWidget(int x, int y) {
        super(
                LinearLayout.vertical().spacing(2),
                x - 8,
                y + 8,
                174,
                Minecraft.getInstance().getWindow().getGuiScaledHeight() - 16,
                CommonComponents.EMPTY);
        this.layout.defaultCellSetting().paddingTop(3).paddingLeft(4);
        this.layout.addChild(PDLImageButton.builder(this::onSettingsButtonPressed)
                .size(14, 14)
                .sprites(SETTINGS_BUTTON)
                .tooltip(Tooltip.create(Component.literal("Configure Project Settings")))
                .build());
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    private void onSettingsButtonPressed(PDLImageButton button) {}

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // TODO(26.1 port, GUI parity): 1.21.1 pushed the side bar behind the rest of the screen; 26.1 layers by draw
        // order, so it now covers whatever the screen drew before it. Checked in the client visual parity pass
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                EDITOR_SIDE_BAR_TEXTURE,
                this.getX(),
                this.getY(),
                0,
                0,
                174,
                this.height,
                this.width,
                16);

        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }
}
