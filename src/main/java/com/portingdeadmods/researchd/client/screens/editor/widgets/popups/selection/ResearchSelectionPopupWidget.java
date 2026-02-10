package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.selection;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLImageButton;
import com.portingdeadmods.researchd.utils.Search;
import com.portingdeadmods.researchd.utils.Spaghetti;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ResearchSelectionPopupWidget extends PopupWidget {
    public static final ResourceLocation BACKGROUND_SPRITE = Researchd.rl("widget/research_selector_widget");

    private final EditBox searchBar;
    private final Search search;
    private final SelectionContainerWidget selectionContainerWidget;
    private final PDLImageButton doneButton;
    private final ResearchSelectorListWidget selectorListWidget;
    private final PopupWidget parentPopupWidget;
    private final Set<ResourceKey<Research>> addedResearches;

    public ResearchSelectionPopupWidget(ResearchSelectorListWidget selectorListWidget, @Nullable PopupWidget parentPopupWidget, Set<ResourceKey<Research>> addedResearches) {
        super(0, 0, 148, 160, CommonComponents.EMPTY);
        this.selectorListWidget = selectorListWidget;
        this.parentPopupWidget = parentPopupWidget;
        this.addedResearches = addedResearches;
        this.search = new Search();
        this.searchBar = this.addRenderableWidget(new EditBox(Minecraft.getInstance().font, 90, 12, CommonComponents.EMPTY));
        this.searchBar.setBordered(false);
        this.searchBar.setEditable(true);
        this.searchBar.setResponder(this::onSearchBarValueChanged);
        this.selectionContainerWidget = this.addRenderableWidget(new SelectionContainerWidget(this, 0, 0, 112, 130, true));
        this.selectionContainerWidget.getItems().removeAll(addedResearches);
        this.doneButton = this.addRenderableWidget(PDLImageButton.builder(this::onDoneClicked)
                .size(14, 14)
                .tooltip(Tooltip.create(Component.literal("Select Research")))
                .sprites(new WidgetSprites(Researchd.rl("editor_checkmark_button"), Researchd.rl("editor_checkmark_button_disabled"), Researchd.rl("editor_checkmark_button_highlighted")))
                .build());
        this.doneButton.active = false;
        this.setPosition(this.getX(), this.getY());
    }

    private void onDoneClicked(PDLImageButton button) {
        ResearchScreen screen = Spaghetti.tryGetResearchScreen();
        screen.openPopupCentered(this.parentPopupWidget);
        Research research = ResearchHelperCommon.getResearch(this.selectionContainerWidget.selectedResearch, Minecraft.getInstance().level);
        this.selectorListWidget.addItem(new ResearchSelectorListWidget.Element.SimpleElement(this.selectionContainerWidget.selectedResearch, research));
        screen.closePopup(this);
    }

    @Override
    protected void onClose() {
        super.onClose();

        ResearchScreen screen = Spaghetti.tryGetResearchScreen();
        screen.openPopupCentered(this.parentPopupWidget);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, this.getX(), this.getY(), 148, 160);

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.searchBar.setX(x + 8);
        this.selectionContainerWidget.setX(x + 7);
        this.doneButton.setX(x + 130);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.searchBar.setY(y + 8);
        this.selectionContainerWidget.setY(y + 23);
        this.doneButton.setY(y + 139 );
    }

    private void onSearchBarValueChanged(String val) {
        Set<ResourceKey<Research>> researchKeys = ResearchHelperCommon.getLevelResearches(Minecraft.getInstance().level).keySet();
        Map<ResourceKey<Research>, Component> researchNames = researchKeys.stream()
                .map(key -> Pair.of(key, ResearchHelperClient.getResearch(key)))
                .map(pair -> Map.entry(pair.left(), ResearchHelperCommon.getResearchName(pair.left(), pair.right())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<ResourceKey<Research>> filteredResearches = new ArrayList<>();
        for (Map.Entry<ResourceKey<Research>, Component> entry : researchNames.entrySet()) {
            if (this.search.matches(entry.getValue().getString(), val)) {
                filteredResearches.add(entry.getKey());
            }
        }
        filteredResearches.removeAll(this.addedResearches);
        this.selectionContainerWidget.setItems(filteredResearches);
    }

    @Override
    public Layout getLayout() {
        return null;
    }

    private static class SelectionContainerWidget extends ContainerWidget<ResourceKey<Research>> {
        public static final WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_background"), Researchd.rl("editor_background_highlighted"));

        private final Map<ResourceKey<Research>, Pair<ClientResearchIcon<?>, Component>> iconsAndNames;
        private final ResearchSelectionPopupWidget parentWidget;
        private ResourceKey<Research> selectedResearch;

        public SelectionContainerWidget(ResearchSelectionPopupWidget parentWidget, int x, int y, int width, int height, boolean renderScroller) {
            super(x, y, width, height, width - 2, 18, Orientation.VERTICAL, 1, 10, new ArrayList<>(), renderScroller);
            this.parentWidget = parentWidget;
            this.iconsAndNames = new HashMap<>();
            this.setItems(ResearchHelperCommon.getLevelResearches(Minecraft.getInstance().level).keySet());
        }

        @Override
        public void setItems(Collection<ResourceKey<Research>> items) {
            super.setItems(new ArrayList<>(items));

            this.iconsAndNames.clear();

            for (ResourceKey<Research> key : this.getItems()) {
                Research research = ResearchHelperCommon.getResearch(key, Minecraft.getInstance().level);
                this.iconsAndNames.put(key, Pair.of(ClientResearchIcon.getClientIcon(research.researchIcon()), ResearchHelperCommon.getResearchName(key, research)));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean clicked = super.mouseClicked(mouseX, mouseY, button);
            if (this.hoveredItem == null && this.isHovered()) {
                this.selectedResearch = null;
                this.parentWidget.doneButton.active = false;
            }
            return clicked;
        }

        @Override
        public void clickedItem(ResourceKey<Research> item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
            this.selectedResearch = item;
            this.parentWidget.doneButton.active = true;
        }

        @Override
        protected int getLeft() {
            return super.getLeft() - 1;
        }

        @Override
        protected int getTop() {
            return super.getTop() - 1;
        }

        @Override
        protected int getScrollerX(float percentage) {
            return super.getScrollerX(percentage);
        }

        @Override
        protected void internalRenderItem(GuiGraphics guiGraphics, ResourceKey<Research> item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
            guiGraphics.blitSprite(SPRITES.get(true, this.isItemHovered(xIndex, yIndex, mouseX, mouseY) || this.selectedResearch == item), left, top, this.getItemWidth(), this.getItemHeight());
            Pair<ClientResearchIcon<?>, Component> pair = this.iconsAndNames.get(item);
            ClientResearchIcon<?> icon = pair.left();
            Component name = pair.right();
            icon.render(guiGraphics, left + 1, top + 1, mouseX, mouseY, 1, 1);
            guiGraphics.drawScrollingString(PopupWidget.getFont(), name, left + 18 + 1, left + this.getItemWidth() - 1, top + 4, -1);
        }

    }
}
