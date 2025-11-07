package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SelectPackSearchBarWidget extends AbstractWidget {
    public static final ResourceLocation SEARCH_BAR_SPRITE = Researchd.rl("editor_search_bar");
    public static final WidgetSprites CREATE_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_create_pack"), Researchd.rl("editor_create_pack"));
    public static final WidgetSprites SELECT_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_select_pack"), Researchd.rl("editor_select_pack"));
    private final LinearLayout layout;
    private final PDLImageButton selectPackDirectoryButton;
    private final PDLImageButton createPackButton;

    public SelectPackSearchBarWidget() {
        super(0, 0, 192, 16, CommonComponents.EMPTY);
        this.layout = LinearLayout.horizontal();

        this.selectPackDirectoryButton = this.layout.addChild(PDLImageButton.builder(this::selectPackDirectory)
                .tooltip(Tooltip.create(Component.literal("Select Pack Directory")))
                .sprites(SELECT_PACK_SPRITES)
                .size(14, 14)
                .pos(128, 0)
                .build());
        this.createPackButton = this.layout.addChild(PDLImageButton.builder(this::createPack)
                .tooltip(Tooltip.create(Component.literal("Create Pack")))
                .sprites(CREATE_PACK_SPRITES)
                .size(14, 14)
                .pos(128 + 14, 0)
                .build());
    }

    private void createPack(PDLImageButton button) {

    }

    private void selectPackDirectory(PDLImageButton button) {

    }

    public LinearLayout getLayout() {
        return layout;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(SEARCH_BAR_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.selectPackDirectoryButton.render(guiGraphics, mouseX, mouseY, partialTick);
        this.createPackButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.selectPackDirectoryButton.setX(x + 128);
        this.createPackButton.setX(x + 128 + 8);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.selectPackDirectoryButton.setY(y);
        this.createPackButton.setY(y);
    }

}
