package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class EditorPopupWidget extends PopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/editor_popup");

    private final LinearLayout layout;

    public EditorPopupWidget() {
        this(0, 0);
    }

    public EditorPopupWidget(int x, int y) {
        super(x, y, 256, 192, CommonComponents.EMPTY);

        Font font = Minecraft.getInstance().font;


        this.layout = LinearLayout.vertical().spacing(6);
        this.layout.addChild(new StringWidget(Component.literal("Editor"), font), LayoutSettings::alignHorizontallyCenter);
        this.layout.addChild(Button.builder(Component.literal("Select Datapack"), this::onAddResearchPressed).size(128, 16).build());
        this.layout.addChild(Button.builder(Component.literal("Create Datapack"), this::onAddResearchPackPressed).size(128, 16).build());

        this.layout.arrangeElements();
        FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
    }

    private void onAddResearchPackPressed(Button button) {

    }

    private void onAddResearchPressed(Button button) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.layout.setX(x);
        FrameLayout.alignInRectangle(this.layout, x, this.getY(), this.width, this.height, 0.5F, 0.25F);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.layout.setY(y);
        FrameLayout.alignInRectangle(this.layout, this.getX(), y, this.width, this.height, 0.5F, 0.25F);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        this.layout.visitWidgets(consumer);
    }
}
