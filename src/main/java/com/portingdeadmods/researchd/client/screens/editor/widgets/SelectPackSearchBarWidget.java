package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.research.widgets.BackgroundStringWidget;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import com.portingdeadmods.researchd.utils.PrettyPath;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SelectPackSearchBarWidget extends AbstractWidget {
    public static final ResourceLocation SEARCH_BAR_SPRITE = Researchd.rl("editor_search_bar");
    public static final WidgetSprites CREATE_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_create_pack"), Researchd.rl("editor_create_pack_highlighted"));
    public static final WidgetSprites SELECT_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_select_pack"), Researchd.rl("editor_select_pack_highlighted"));
    public static final MutableComponent SELECT_OR_CREATE_PACK_TEXT = Component.literal("<Select or Create Pack>");
    private final PDLImageButton selectPackDirectoryButton;
    private final PDLImageButton createPackButton;
    private Path fullPath;

    public SelectPackSearchBarWidget(PrettyPath path, PDLButton.OnPress<PDLImageButton> onSelectPressed, PDLButton.OnPress<PDLImageButton> onCreatePressed) {
        this(0, 0, path, onSelectPressed, onCreatePressed);
    }

    public SelectPackSearchBarWidget(int x, int y, PrettyPath path, PDLButton.OnPress<PDLImageButton> onSelectPressed, PDLButton.OnPress<PDLImageButton> onCreatePressed) {
        super(x, y, 156, 16, SelectPackSearchBarWidget.getText(path));

        if (path != null) {
            this.fullPath = path.fullPath();
        }

        this.selectPackDirectoryButton = PDLImageButton.builder(onSelectPressed)
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

    private static Component getText(PrettyPath path) {
        Component text;
        if (path != null) {
            String pathStr = path.shortPath().toString();
            text = pathStr.isEmpty() ? SELECT_OR_CREATE_PACK_TEXT : Component.literal(pathStr);
        } else {
            text = SELECT_OR_CREATE_PACK_TEXT;
        }
        return text;
    }

    public void updateSearchBarText(PrettyPath datapackDir) {
        this.setMessage(SelectPackSearchBarWidget.getText(datapackDir));
        if (datapackDir != null) {
            this.fullPath = datapackDir.fullPath();
        } else {
            this.fullPath = null;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(SEARCH_BAR_SPRITE, this.getX(), this.getY(), 156, 16);

        this.createPackButton.render(guiGraphics, mouseX, mouseY, partialTick);
        this.selectPackDirectoryButton.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawScrollingString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 2, this.getX() + 156 - 32 - 1, this.getY() + 2 + (14 - Minecraft.getInstance().font.lineHeight) / 2, FastColor.ARGB32.color(255, 255, 255));

        if (!(this.createPackButton.isHovered() || this.selectPackDirectoryButton.isHovered()) && this.fullPath != null && this.isHovered()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(this.fullPath.toString()), mouseX, mouseY);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.createPackButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (this.selectPackDirectoryButton.mouseClicked(mouseX, mouseY, button)) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

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

    public Path getFullPath() {
        return fullPath;
    }

    public boolean hasPack() {
        return fullPath != null;
    }

}
