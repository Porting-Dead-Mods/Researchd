/**
 * Backend code for research screen, handles much of the infrastructure
 * for research screens like popups, dropdowns and tooltips. In the future
 * we might abstract this even further and move it to pdl.
 */
package com.portingdeadmods.researchd.client.screens.research;

import com.portingdeadmods.researchd.client.screens.lib.ClickDispatch;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import java.util.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ArrayListDeque;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractResearchScreen extends Screen {
    private static List<Component> tooltip = null;

    public static void setTooltip(List<Component> tooltipNew) {
        tooltip = tooltipNew;
    }

    protected final List<PopupWidget> popupWidgets;
    protected @Nullable PopupWidget focusedPopupWidget;

    protected @Nullable DropDownWidget<?> dropDownWidget;

    public AbstractResearchScreen(Component title) {
        super(title);

        this.popupWidgets = new ArrayListDeque<>();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (!this.popupWidgets.isEmpty() && this.focusedPopupWidget != null) {
            this.closePopup(this.focusedPopupWidget);
            return false;
        }
        return true;
    }

    public <W extends PopupWidget> W openPopupCentered(W widget) {
        int x = (this.width - widget.getWidth()) / 2;
        int y = (this.height - widget.getHeight()) / 2;
        widget.setPosition(x, y);

        return this.openPopup(widget);
    }

    public <W extends PopupWidget> W openPopup(W widget) {
        if (!this.popupWidgets.contains(widget)) {
            this.popupWidgets.add(widget);
            widget.open();
        }
        this.setFocused(widget);
        return widget;
    }

    public <W extends PopupWidget> void closePopup(W widget) {
        widget.close();
        this.popupWidgets.remove(widget);
    }

    private Optional<GuiEventListener> getPopupChildAt(double mouseX, double mouseY) {
        if (this.focusedPopupWidget != null && this.focusedPopupWidget.isHovered())
            return Optional.of(this.focusedPopupWidget);

        for (PopupWidget popupWidget : this.popupWidgets) {
            for (AbstractWidget widget : popupWidget.getWidgets()) {
                if (widget.isMouseOver(mouseX, mouseY)) {
                    return Optional.of(widget);
                }
            }
        }

        return Optional.empty();
    }

    public void setDropDown(@Nullable DropDownWidget<?> dropDownWidget) {
        this.dropDownWidget = dropDownWidget;
        if (this.dropDownWidget != null) {
            this.dropDownWidget.rebuildOptions();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);

        if (listener instanceof PopupWidget popupWidget && this.popupWidgets.contains(popupWidget)) {
            this.focusedPopupWidget = popupWidget;

            this.popupWidgets.remove(popupWidget);
            this.popupWidgets.addLast(popupWidget);
        } else if (listener == null) {
            this.focusedPopupWidget = null;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int button = event.button();
        List<PopupWidget> reversed = new ArrayList<>(this.popupWidgets.reversed());
        for (PopupWidget popupWidget : reversed) {
            for (AbstractWidget widget : popupWidget.getWidgets()) {
                if (widget.mouseClicked(event, doubleClick)) {
                    this.setFocused(widget);
                    if (button == 0) {
                        this.setDragging(true);
                    }

                    return true;
                }
            }
        }
        return ClickDispatch.mouseClicked(this, event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.getPopupChildAt(mouseX, mouseY)
                .filter(widget -> widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
                .isPresent()) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (this.getPopupChildAt(mouseX, mouseY)
                .filter(widget -> widget.mouseDragged(event, dragX, dragY))
                .isPresent()) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    protected void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (tooltip != null) {
            guiGraphics.setComponentTooltipForNextFrame(
                    com.portingdeadmods.researchd.utils.GuiUtils.getFont(), tooltip, mouseX, mouseY);
        }
    }

    protected abstract void renderContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick);

    @Override
    public final void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        setTooltip(null);

        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        this.renderContents(guiGraphics, mouseX, mouseY, partialTick);

        // Each popup goes on its own stratum so it covers the screen and the popups opened before it
        for (PopupWidget popupWidget : this.popupWidgets) {
            guiGraphics.nextStratum();

            popupWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

            for (AbstractWidget widget : popupWidget.getWidgets()) {
                widget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY, partialTick);
    }
}
