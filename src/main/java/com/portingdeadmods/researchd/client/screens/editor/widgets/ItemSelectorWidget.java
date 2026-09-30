package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ItemSelectorPopupWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.DefaultItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.ItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.TagItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.utils.SpaghettiClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ItemSelectorWidget extends AbstractWidget {
    public static final Identifier EDIT_ELEMENT_HOVER_SPRITE = Researchd.rl("edit_element_hover");

    @Nullable private final PopupWidget parentPopupWidget;

    /** Shown and used for icons; stacks keep their components (a Research Pack is one item told apart by its component) */
    private List<ItemStack> selected;
    /** The ingredient the selection came from, kept so an unchanged (e.g. tag) ingredient is saved back as it was */
    @Nullable private Ingredient selectedIngredient;

    private final BiFunction<ItemSelectorWidget, @Nullable PopupWidget, ? extends ItemSelectorPopupWidget>
            popupWidgetFactory;
    private Consumer<List<ItemStack>> responder;

    public ItemSelectorWidget(
            @Nullable PopupWidget parentPopupWidget,
            int x,
            int y,
            int width,
            int height,
            boolean tagSelector,
            boolean selectMultiple) {
        this(parentPopupWidget, x, y, width, height, List.of(new ItemStack(Items.DIRT)), (self, parent) -> {
            List<ItemSelectorCategory> categories = new ArrayList<>();
            Collections.addAll(categories, DefaultItemSelectorCategory.values());
            if (tagSelector) categories.add(TagItemSelectorCategory.INSTANCE);
            return new ItemSelectorPopupWidget(
                    self, parent, categories, DefaultItemSelectorCategory.getDefault(), 0, 0);
        });
    }

    public ItemSelectorWidget(
            @Nullable PopupWidget parentPopupWidget,
            int x,
            int y,
            int width,
            int height,
            List<ItemStack> defaultSelected,
            BiFunction<ItemSelectorWidget, @Nullable PopupWidget, ? extends ItemSelectorPopupWidget>
                    popupWidgetFactory) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.popupWidgetFactory = popupWidgetFactory;
        this.parentPopupWidget = parentPopupWidget;
        this.setTooltip(Tooltip.create(Component.literal("Select Icon")));
        this.selected = defaultSelected;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight());

        if (!this.selected.isEmpty()) {
            guiGraphics.item(
                    this.selected.getFirst(),
                    this.getX() + (this.getWidth() - 16) / 2,
                    this.getY() + (this.getWidth() - 16) / 2);
        }
        if (this.isHovered()) {
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    EDIT_ELEMENT_HOVER_SPRITE,
                    this.getX() + (this.getWidth() - 14) / 2,
                    this.getY() + (this.getHeight() - 14) / 2,
                    14,
                    14);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isHovered()) {
            ResearchScreen screen = SpaghettiClient.tryGetResearchScreen();
            if (this.parentPopupWidget != null) {
                screen.closePopup(this.parentPopupWidget);
            }
            screen.openPopupCentered(this.popupWidgetFactory.apply(this, this.parentPopupWidget));
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    public void setSelected(Ingredient selected, boolean respond) {
        this.setSelected(selected.items().map(ItemStack::new).toList(), selected, respond);
    }

    public void setSelected(List<ItemStack> selected, boolean respond) {
        this.setSelected(selected, null, respond);
    }

    private void setSelected(List<ItemStack> selected, @Nullable Ingredient selectedIngredient, boolean respond) {
        this.selected = selected;
        this.selectedIngredient = selectedIngredient;
        if (respond && responder != null) {
            this.responder.accept(this.selected);
        }
    }

    public List<ItemStack> getSelectedStacks() {
        return this.selected;
    }

    /** The selection as an ingredient, matching the selected items regardless of their components */
    public Optional<Ingredient> getSelected() {
        if (this.selectedIngredient != null) {
            return Optional.of(this.selectedIngredient);
        }
        List<ItemStack> nonEmpty =
                this.selected.stream().filter(stack -> !stack.isEmpty()).toList();
        return nonEmpty.isEmpty()
                ? Optional.empty()
                : Optional.of(Ingredient.of(nonEmpty.stream().map(ItemStack::getItem)));
    }

    public ItemResearchIcon createIcon() {
        return new ItemResearchIcon(this.selected.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStackTemplate::fromNonEmptyStack)
                .toList());
    }

    public void setResponder(Consumer<List<ItemStack>> responder) {
        this.responder = responder;
    }
}
