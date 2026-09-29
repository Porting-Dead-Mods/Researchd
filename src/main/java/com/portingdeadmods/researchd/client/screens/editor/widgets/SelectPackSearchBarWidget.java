package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.editmode.PackLocation;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PDLButton;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PDLImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class SelectPackSearchBarWidget extends AbstractWidget {
    public static final Identifier SEARCH_BAR_SPRITE = Researchd.rl("editor_search_bar");
    public static final WidgetSprites CREATE_PACK_SPRITES =
            new WidgetSprites(Researchd.rl("editor_create_pack"), Researchd.rl("editor_create_pack_highlighted"));
    public static final WidgetSprites SELECT_PACK_SPRITES =
            new WidgetSprites(Researchd.rl("editor_select_pack"), Researchd.rl("editor_select_pack_highlighted"));
    public static final MutableComponent SELECT_OR_CREATE_PACK_TEXT = Component.literal("<Select or Create Pack>");
    private final PDLImageButton selectPackDirectoryButton;
    private final PDLImageButton createPackButton;
    private final SelectPackDropDownWidget dropDownWidget;
    private PackLocation selectedPack;

    public SelectPackSearchBarWidget(
            PackLocation pack, PackType type, PDLButton.OnPress<PDLImageButton> onCreatePressed) {
        this(0, 0, pack, type, onCreatePressed);
    }

    public SelectPackSearchBarWidget(
            int x, int y, PackLocation pack, PackType type, PDLButton.OnPress<PDLImageButton> onCreatePressed) {
        super(x, y, 156, 16, CommonComponents.EMPTY);

        this.selectedPack = pack;

        this.dropDownWidget = new SelectPackDropDownWidget(this, type);
        this.dropDownWidget.setVisible(false);
        this.dropDownWidget.rebuildOptions();
        this.selectPackDirectoryButton = PDLImageButton.builder(
                        btn -> this.dropDownWidget.setVisible(!this.dropDownWidget.isVisible()))
                .tooltip(Tooltip.create(Component.literal("Select Pack Directory")))
                .sprites(SELECT_PACK_SPRITES)
                .size(14, 14)
                .pos(128, 10)
                .build();
        this.createPackButton = PDLImageButton.builder(onCreatePressed)
                .tooltip(Tooltip.create(Component.literal("Create Pack")))
                .sprites(CREATE_PACK_SPRITES)
                .size(14, 14)
                .pos(128 + 14, 10)
                .build();
    }

    private static Component getText(PackLocation packLocation) {
        Component text;
        if (packLocation != null) {
            String pathStr = packLocation.rootPackName() + "/" + packLocation.namespace();
            text = pathStr.isEmpty() ? SELECT_OR_CREATE_PACK_TEXT : Component.literal(pathStr);
        } else {
            text = SELECT_OR_CREATE_PACK_TEXT;
        }
        return text;
    }

    public void updateSelectedPack(PackLocation pack) {
        this.selectedPack = pack;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_BAR_SPRITE, this.getX(), this.getY(), 156, 16);

        this.createPackButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.selectPackDirectoryButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawScrollingString(
                guiGraphics.textRenderer(),
                Minecraft.getInstance().font,
                getText(this.selectedPack),
                this.getX() + 2,
                this.getX() + 156 - 32 - 1,
                this.getY() + 2 + (14 - Minecraft.getInstance().font.lineHeight) / 2);

        if (!(this.createPackButton.isHovered() || this.selectPackDirectoryButton.isHovered())
                && this.selectedPack != null
                && this.isHovered()) {
            guiGraphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    Component.literal(this.selectedPack.rootPath().toString()),
                    mouseX,
                    mouseY);
        }

        // TODO(26.1 port, GUI parity): 26.1 GUI layering follows draw order, so the drop-down no longer sits above
        // widgets drawn after it; checked in the client visual parity pass
        this.dropDownWidget.render(
                guiGraphics, this.getX(), this.getY() + this.getHeight(), mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.createPackButton.mouseClicked(event, doubleClick)) return true;
        if (this.selectPackDirectoryButton.mouseClicked(event, doubleClick)) return false;
        if (this.dropDownWidget.isVisible() && this.dropDownWidget.mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void setX(int x) {
        super.setX(x);

        this.selectPackDirectoryButton.setX(x + 128 + 14 - 1);
        this.createPackButton.setX(x + 128 - 1);
    }

    @Override
    public void setY(int y) {
        super.setY(y);

        this.selectPackDirectoryButton.setY(y + 1);
        this.createPackButton.setY(y + 1);
    }

    public boolean hasPack() {
        return this.selectedPack != null;
    }
}
