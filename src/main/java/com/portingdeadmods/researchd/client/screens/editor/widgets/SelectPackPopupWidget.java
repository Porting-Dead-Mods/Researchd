package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.ChatFormatting;
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

public class SelectPackPopupWidget extends PopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/editor_popup");

    private final LinearLayout layout;

    public SelectPackPopupWidget() {
        this(0, 0);
    }

    public SelectPackPopupWidget(int x, int y) {
        super(x, y, 256, 192, CommonComponents.EMPTY);

        Font font = Minecraft.getInstance().font;

        this.layout = LinearLayout.vertical().spacing(4);
        this.layout.addChild(new SpacerElement(0, 1));
        this.layout.addChild(new StringWidget(Component.literal("Editor"), font), s -> s.alignHorizontallyCenter().alignVerticallyTop());
        this.layout.addChild(new SpacerElement(0, 32));
        this.layout.addChild(new StringWidget(Component.literal("Datapack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
        this.layout.addChild(new SelectPackSearchBarWidget().getLayout());
        this.layout.addChild(new StringWidget(Component.literal("Resource Pack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
        this.layout.addChild(new SelectPackSearchBarWidget().getLayout());
        this.layout.addChild(Button.builder(Component.literal("Start Editing"), this::onAddResearchPackPressed).size(128, 16).build(), s -> s.alignHorizontallyCenter().alignVerticallyBottom());

        this.layout.arrangeElements();
        //FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
        FrameLayout.alignInDimension(x, width, this.layout.getWidth(), this.layout::setX, 0.5f);
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
        //FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
        FrameLayout.alignInDimension(x, width, this.layout.getWidth(), this.layout::setX, 0.5f);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.layout.setY(y);
        //FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
        //FrameLayout.alignInDimension(y, height, this.layout.getHeight(), this.layout::setY, 0.5f);
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
