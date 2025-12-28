package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ResearchMethodTypePopupWidget extends PopupWidget {
    private final ResearchMethodTypeContainerWidget containerWidget;
    @Nullable
    private final PopupWidget parentPopupWidget;

    public ResearchMethodTypePopupWidget(@Nullable PopupWidget parentPopupWidget, Component message) {
        super(0, 0, 160, 64, message);
        this.parentPopupWidget = parentPopupWidget;
        this.containerWidget = this.addRenderableWidget(new ResearchMethodTypeContainerWidget(parentPopupWidget, this, 160 - 16, 64 - 16));
    }

    @Override
    protected void onPositionChanged(int x, int y) {
        super.onPositionChanged(x, y);

        this.containerWidget.setPosition(x + 7, y + 7);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(EditorSharedSprites.EDITOR_WIDGET_BACKGROUND_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public Layout getLayout() {
        return null;
    }

    public static class ResearchMethodTypeContainerWidget extends ContainerWidget<ResearchMethodListType> {
        @Nullable
        private final PopupWidget parentPopupWidget;
        private final ResearchMethodTypePopupWidget popupWidget;

        public ResearchMethodTypeContainerWidget(@Nullable PopupWidget parentPopupWidget, ResearchMethodTypePopupWidget popupWidget, int width, int height) {
            super(width, height, 48, 48, Orientation.HORIZONTAL, ResearchMethodListType.values().length, 1, List.of(ResearchMethodListType.values()), true);
            this.parentPopupWidget = parentPopupWidget;
            this.popupWidget = popupWidget;
        }

        @Override
        public void clickedItem(ResearchMethodListType item, int xIndex, int yIndex, int left, int top, int mouseX,
                                int mouseY) {
            ResearchScreen screen = Spaghetti.tryGetResearchScreen();
            screen.closePopup(this.popupWidget);
            screen.openPopupCentered(new ResearchMethodSelectionPopupWidget(this.parentPopupWidget, item));
        }

        @Override
        protected void internalRenderItem(GuiGraphics guiGraphics, ResearchMethodListType item, int xIndex, int yIndex,
                                          int left, int top, int mouseX, int mouseY) {
            guiGraphics.blitSprite(EditorSharedSprites.EDITOR_BACKGROUND_SPRITES.get(true, this.isItemHovered(xIndex, yIndex, mouseX, mouseY)), left, top, this.getItemWidth(), this.getItemHeight());
            guiGraphics.drawCenteredString(getFont(), item.getName(), left + this.getItemWidth() / 2, top + (this.getItemHeight() - getFont().lineHeight) / 2, -1);
        }
    }

    public enum ResearchMethodListType {
        SINGLE(() -> Component.literal("Single")),
        AND(() -> Component.literal("And")),
        OR(() -> Component.literal("Or"));

        private final Supplier<Component> name;

        ResearchMethodListType(Supplier<Component> name) {
            this.name = name;
        }

        public Component getName() {
            return name.get();
        }
    }
}
