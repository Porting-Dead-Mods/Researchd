package com.portingdeadmods.researchd.client.impl.editor;

import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.Nullable;

public record EditorContextImpl(PDLButton createButton, ResearchScreen parentScreen, @Nullable PopupWidget parentPopupWidget, int widgetWidth,
                                int widgetHeight, int innerWidth, int innerHeight, int padding) implements EditorContext {
    @Override
    public boolean isCreateButtonActive() {
        return createButton.active;
    }

    @Override
    public void setCreateButtonActive(boolean active) {
        this.createButton.active = active;
    }

    @Override
    public void setCreateButtonTooltip(Tooltip tooltip) {
        this.createButton.setTooltip(tooltip);
    }
}
