package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ResearchSelectionPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.utils.Spaghetti;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Widget for selecting a list of elements horizontally, scrollable with a popup for selecting the element
public class ResearchSelectorListWidget extends ContainerWidget<ResearchSelectorListWidget.Element> {
    public static final ResourceLocation BACKGROUND_SPRITES = Researchd.rl("editor_background_research_list");

    private final PopupWidget parentPopupWidget;
    private UniqueArray<Element> items;

    public ResearchSelectorListWidget(@Nullable PopupWidget parentPopupWidget, int width, int height, Collection<Element> items, boolean renderScroller) {
        super(width, height, 18, 18, Orientation.HORIZONTAL, width, 1, items, renderScroller);
        this.parentPopupWidget = parentPopupWidget;
        this.items = new UniqueArray<>(items);
        this.getItems().add(Element.SelectorElement.INSTANCE);
    }

    @Override
    protected int getScissorsWidth() {
        return this.getWidth();
    }

    public void addItem(Element item) {
        if (!this.getItems().isEmpty()) {
            this.getItems().set(this.getItems().size() - 1, item);
            this.getItems().addLast(Element.SelectorElement.INSTANCE);
        } else {
            this.getItems().add(item);
            this.getItems().add(Element.SelectorElement.INSTANCE);
        }
    }

    public void removeItem(Element element) {
        if (!this.getItems().isEmpty()) {
            this.getItems().removeLast();
            this.getItems().remove(element);
            this.getItems().add(Element.SelectorElement.INSTANCE);
            this.scrollOffset = 0;
        }
    }

    public List<ResourceKey<Research>> getResearches() {
        return this.getItems().stream()
                .filter(elem -> elem instanceof Element.SimpleElement)
                .map(elem -> ((Element.SimpleElement) elem).researchKey())
                .toList();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blitSprite(BACKGROUND_SPRITES, this.getX(), this.getY(), this.getWidth() + 2, this.getHeight());

        super.renderWidget(guiGraphics, mouseX, mouseY, v);
    }

    @Override
    public void setItems(Collection<Element> items) {
        this.items = new UniqueArray<>(items);
    }

    @Override
    public UniqueArray<Element> getItems() {
        return this.items;
    }

    @Override
    public void clickedItem(Element item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        if (item instanceof Element.SelectorElement) {
            ResearchScreen screen = Spaghetti.tryGetResearchScreen();
            if (this.parentPopupWidget != null) {
                screen.closePopup(this.parentPopupWidget);
            }
            screen.openPopupCentered(new ResearchSelectionPopupWidget(this, this.parentPopupWidget, Set.copyOf(this.getResearches())));
        } else {
            this.removeItem(item);
        }
    }

    @Override
    protected void internalRenderItem(GuiGraphics guiGraphics, Element item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        item.render(guiGraphics, left, top, this.getItemWidth(), this.getItemHeight(), this.isItemHovered(xIndex, yIndex, mouseX, mouseY), mouseX, mouseY, 1);
    }

    @Override
    protected void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        super.renderTooltips(guiGraphics, mouseX, mouseY, v);

        if (this.hoveredItem instanceof Element.SimpleElement(ResourceKey<Research> researchKey, Research research)) {
            guiGraphics.renderTooltip(PopupWidget.getFont(), ResearchHelperCommon.getResearchName(researchKey, research), mouseX, mouseY);
        }

    }

    public sealed interface Element permits Element.SimpleElement, Element.SelectorElement {
        WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_background"), Researchd.rl("editor_background_highlighted"));

        void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick);

        record SimpleElement(ResourceKey<Research> researchKey, Research research) implements Element {
            public static final ResourceLocation REMOVE_ELEMENT_HOVER_SPRITE = Researchd.rl("remove_element_hover");

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blitSprite(SPRITES.get(true, hovered), x, y, width, height);
                ClientResearchIcon.getClientIcon(research.researchIcon()).render(guiGraphics, x + 1, y + 1, mouseX, mouseY, 1, partialTick);
                if (hovered) {
                    PoseStack poseStack  = guiGraphics.pose();
                    poseStack.pushPose();
                    {
                        poseStack.translate(0, 0, 160);
                        guiGraphics.blitSprite(REMOVE_ELEMENT_HOVER_SPRITE, x + 2, y + 2, 14, 14);
                    }
                    poseStack.popPose();
                }
            }
        }

        final class SelectorElement implements Element {
            public static final WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_select_research"), Researchd.rl("editor_select_research_highlighted"));
            public static final SelectorElement INSTANCE = new SelectorElement();

            private SelectorElement() {
            }

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blitSprite(SPRITES.get(true, hovered), x, y, width, height);
            }
        }
    }
}
