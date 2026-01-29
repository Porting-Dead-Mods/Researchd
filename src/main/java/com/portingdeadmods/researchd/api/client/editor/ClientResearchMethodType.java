package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import org.jetbrains.annotations.Nullable;

public interface ClientResearchMethodType {
    default int getHeight() {
        return 128;
    }

    ResearchMethodType type();

    void buildLayout(RememberingLinearLayout layout, ClientResearchMethodType.Context context);

    ResearchMethod createResearchEffect(RememberingLinearLayout layout);

    static ClientResearchMethodType getClientMethodType(ResearchMethodType methodType) {
        return ResearchdClient.CLIENT_RESEARCH_METHOD_TYPES.get(methodType.id());
    }

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
    }
}
