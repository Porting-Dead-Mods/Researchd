package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ItemSelectorPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.DefaultItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.ItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.TagItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class ItemSelectorWidget extends AbstractWidget {
    public static final ResourceLocation EDIT_ELEMENT_HOVER_SPRITE = Researchd.rl("edit_element_hover");
    @Nullable
    private final PopupWidget parentPopupWidget;
    private List<ItemStack> selected;
    private final BiFunction<ItemSelectorWidget, @Nullable PopupWidget, ? extends ItemSelectorPopupWidget> popupWidgetFactory;

    public ItemSelectorWidget(@Nullable PopupWidget parentPopupWidget, int x, int y, int width, int height, boolean tagSelector, boolean selectMultiple) {
        this(parentPopupWidget, x, y, width, height, List.of(new ItemStack(Items.DIRT)), (self, parent) -> {
            List<ItemSelectorCategory> categories = new ArrayList<>();
            Collections.addAll(categories, DefaultItemSelectorCategory.values());
            if (tagSelector) categories.add(TagItemSelectorCategory.INSTANCE);
            return new ItemSelectorPopupWidget(self, parent, categories, DefaultItemSelectorCategory.getDefault(), 0, 0);
        });
    }

    public ItemSelectorWidget(@Nullable PopupWidget parentPopupWidget, int x, int y, int width, int height, List<ItemStack> defaultSelected, BiFunction<ItemSelectorWidget, @Nullable PopupWidget, ? extends ItemSelectorPopupWidget> popupWidgetFactory) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.popupWidgetFactory = popupWidgetFactory;
        this.parentPopupWidget = parentPopupWidget;
        this.setTooltip(Tooltip.create(Component.literal("Select Icon")));
        this.selected = defaultSelected;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (this.selected != null) {
            guiGraphics.renderItem(this.selected.getFirst(), this.getX() + (this.getWidth() - 16) / 2, this.getY() + (this.getWidth() - 16) / 2);
        }
        if (this.isHovered()) {
            PoseStack poseStack  = guiGraphics.pose();
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 160);
                guiGraphics.blitSprite(EDIT_ELEMENT_HOVER_SPRITE, this.getX() + (this.getWidth() - 14) / 2, this.getY() + (this.getHeight() - 14) / 2, 14, 14);
            }
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered()) {
            ResearchScreen screen = Spaghetti.tryGetResearchScreen();
            if (this.parentPopupWidget != null) {
                screen.closePopup(this.parentPopupWidget);
            }
            screen.openPopupCentered(this.popupWidgetFactory.apply(this, this.parentPopupWidget));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setSelected(List<ItemStack> selected) {
        this.selected = selected;
    }

    public List<ItemStack> getSelected() {
        return selected;
    }

    public ItemResearchIcon createIcon() {
        return new ItemResearchIcon(this.getSelected());
    }

    public Ingredient createIngredient() {
        return Ingredient.of(selected.stream());
    }

}
