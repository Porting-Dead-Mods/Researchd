package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public interface LayoutWidget<L extends Layout> {
    L getLayout();

    Iterable<? extends LayoutElement> getElements();

    default void arrangeElements() {
        if (this.getLayout() != null) {
            this.getLayout().arrangeElements();
        }
    }

    default void renderElements(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof Renderable renderable) {
                renderable.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
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

    default boolean mouseClickedElements(MouseButtonEvent event, boolean doubleClick) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseClicked(event, doubleClick)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean mouseReleasedElements(MouseButtonEvent event) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseReleased(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean mouseDraggedElements(MouseButtonEvent event, double dragX, double dragY) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.mouseDragged(event, dragX, dragY)) {
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

    default boolean keyPressedElements(KeyEvent event) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.keyPressed(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean keyReleasedElements(KeyEvent event) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.keyReleased(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean charTypedElements(CharacterEvent event) {
        for (LayoutElement child : this.getElements()) {
            if (child instanceof GuiEventListener guiEventListener) {
                if (guiEventListener.charTyped(event)) {
                    return true;
                }
            }
        }
        return false;
    }
}
