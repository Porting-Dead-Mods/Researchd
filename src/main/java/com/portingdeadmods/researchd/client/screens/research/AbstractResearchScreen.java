/**
 * Backend code for research screen, handles much of the infrastructure
 * for research screens like popups, dropdowns and tooltips. In the future
 * we might abstract this even further and move it to pdl.
 */
package com.portingdeadmods.researchd.client.screens.research;


import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ArrayListDeque;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<PopupWidget> reversed = new ArrayList<>(this.popupWidgets.reversed());
        for (PopupWidget popupWidget : reversed) {
            for (AbstractWidget widget : popupWidget.getWidgets()) {
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(widget);
                    if (button == 0) {
                        this.setDragging(true);
                    }

                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.getPopupChildAt(mouseX, mouseY).filter(widget -> widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)).isPresent()) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.getPopupChildAt(mouseX, mouseY).filter(widget -> widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)).isPresent()) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        setTooltip(null);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        {
            poseStack.translate(0, 0, 300);
            for (PopupWidget popupWidget : this.popupWidgets) {
                poseStack.translate(0, 0, 100);

                popupWidget.render(guiGraphics, mouseX, mouseY, partialTick);

                for (AbstractWidget widget : popupWidget.getWidgets()) {
                    widget.render(guiGraphics, mouseX, mouseY, partialTick);
                }

            }

            if (tooltip != null) {
                guiGraphics.renderComponentTooltip(com.portingdeadmods.researchd.utils.GuiUtils.getFont(), tooltip, mouseX, mouseY);
            }
        }
        poseStack.popPose();

    }
}
