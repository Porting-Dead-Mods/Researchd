package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.research.widgets.BackgroundStringWidget;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
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
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SelectPackSearchBarWidget extends LinearLayout {
    public static final ResourceLocation SEARCH_BAR_SPRITE = Researchd.rl("editor_search_bar");
    public static final WidgetSprites CREATE_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_create_pack"), Researchd.rl("editor_create_pack_highlighted"));
    public static final WidgetSprites SELECT_PACK_SPRITES = new WidgetSprites(Researchd.rl("editor_select_pack"), Researchd.rl("editor_select_pack_highlighted"));
    public static final MutableComponent SELECT_OR_CREATE_PACK_TEXT = Component.literal("<Select or Create Pack>").withStyle(ChatFormatting.GRAY);
    private final PDLImageButton selectPackDirectoryButton;
    private final PDLImageButton createPackButton;

    public SelectPackSearchBarWidget(Path path, PDLButton.OnPress<PDLImageButton> onSelectPressed, PDLButton.OnPress<PDLImageButton> onCreatePressed) {
        super(160, 16, Orientation.HORIZONTAL);

        Component text;
        if (path != null) {
            String pathStr = path.toString();
            text = pathStr.isEmpty() ? SELECT_OR_CREATE_PACK_TEXT : Component.literal(pathStr);
        } else {
            text = SELECT_OR_CREATE_PACK_TEXT;
        }

        this.addChild(new BackgroundStringWidget(128, 16, text, Minecraft.getInstance().font, SEARCH_BAR_SPRITE));
        this.selectPackDirectoryButton = this.addChild(PDLImageButton.builder(onSelectPressed)
                .tooltip(Tooltip.create(Component.literal("Select Pack Directory")))
                .sprites(SELECT_PACK_SPRITES)
                .size(14, 14)
                .pos(128, 10)
                .build());
        this.createPackButton = this.addChild(PDLImageButton.builder(onCreatePressed)
                .tooltip(Tooltip.create(Component.literal("Create Pack")))
                .sprites(CREATE_PACK_SPRITES)
                .size(14, 14)
                .pos(128 + 14, 10)
                .build());
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

}
