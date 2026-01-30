package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ClientResearch {
    void buildLayout(RememberingLinearLayout layout, Context context);

    Research createResearch(RememberingLinearLayout layout);

    ResourceLocation createId(RememberingLinearLayout layout);

    void updateResearch(RememberingLinearLayout layout, Context context);

    Result<Unit, Exception> validResearch(RememberingLinearLayout layout);

    record Context(PDLButton createButton, ResearchScreen parentScreen, @Nullable PopupWidget parentPopupWidget, int widgetWidth,
                   int widgetHeight, int innerWidth, int innerHeight, int padding) implements ClientDataCreationContext {
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

}
