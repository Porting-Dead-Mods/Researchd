package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.lib.widgets.AbstractLayoutWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagCreationWidget extends AbstractLayoutWidget<Layout> {
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);
    private final StringWidget stringWidget;
    private final EditBox tagEditBox;
    private final CycledItemRenderer itemRenderer;

    public TagCreationWidget(int x, int y, int width, int height) {
        super(null, x, y, width, height, CommonComponents.EMPTY);
        this.stringWidget = this.addRenderableWidget(new StringWidget(Component.literal("Hello"), PopupWidget.getFont()));
        this.itemRenderer = new CycledItemRenderer();
        this.tagEditBox = this.addRenderableWidget(new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 72, 16, CommonComponents.EMPTY));
        this.tagEditBox.setResponder(this::onValueChanged);
        this.tagEditBox.setValue("#");
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        this.itemRenderer.render(guiGraphics, this.getX() + 20, this.getY() + 20);
        this.itemRenderer.tick(partialTick);
    }

    private void onValueChanged(String val) {
        String newVal = val.strip();
        if (newVal.startsWith("#")) {
            newVal = val.substring(1);
        } else {
            this.tagEditBox.setValue("#" + newVal);
        }
        Optional<HolderSet.Named<Item>> _tag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, ResourceLocation.parse(newVal)));
        if (_tag.isEmpty()) {
            this.tagEditBox.setTextColor(ChatFormatting.RED.getColor());
            this.itemRenderer.setItems(List.of());
        } else {
            this.tagEditBox.setTextColor(-1);
            HolderSet.Named<Item> tag = _tag.get();
            List<ItemStack> items = new ArrayList<>();
            for (Holder<Item> itemHolder : tag) {
                items.add(itemHolder.value().getDefaultInstance());
            }
            this.itemRenderer.setItems(items);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.stringWidget.setX(x);
        this.tagEditBox.setX(x + 7);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.stringWidget.setY(y);
        this.tagEditBox.setY(y + 7);
    }
}
