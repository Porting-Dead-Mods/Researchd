package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;

public interface LayoutWidget<L extends Layout> {
    L getLayout();
    
    Iterable<? extends LayoutElement> getElements();

    default void arrangeElements() {
        this.getLayout().arrangeElements();
    }

    default void renderElements(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    default void mouseMovedElements(double mouseX, double mouseY) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                guiEventListener.mouseMoved(mouseX, mouseY);
            }
        }
    }

    default boolean mouseClickedElements(double mouseX, double mouseY, int button) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean mouseReleasedElements(double mouseX, double mouseY, int button) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean mouseDraggedElements(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean mouseScrolledElements(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean keyPressedElements(int keyCode, int scanCode, int modifiers) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean keyReleasedElements(int keyCode, int scanCode, int modifiers) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.keyReleased(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean charTypedElements(char codePoint, int modifiers) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }
}
