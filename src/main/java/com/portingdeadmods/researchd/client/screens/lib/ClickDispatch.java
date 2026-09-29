package com.portingdeadmods.researchd.client.screens.lib;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;

public final class ClickDispatch {
    private ClickDispatch() {}

    // TODO(26.1 port, GUI parity): drop this if the screens stop nesting buttons inside widgets
    /**
     * 26.1's {@link ContainerEventHandler#mouseClicked} only offers a click to the first child under the mouse. The
     * Researchd screens put buttons inside bigger widgets that come first, so this offers the click to each child in
     * turn until one takes it, as 1.21.1 did.
     */
    public static boolean mouseClicked(ContainerEventHandler handler, MouseButtonEvent event, boolean doubleClick) {
        for (GuiEventListener child : handler.children()) {
            if (child.mouseClicked(event, doubleClick)) {
                if (child.shouldTakeFocusAfterInteraction()) {
                    handler.setFocused(child);
                    if (event.button() == 0) {
                        handler.setDragging(true);
                    }
                }

                return true;
            }
        }

        return false;
    }
}
