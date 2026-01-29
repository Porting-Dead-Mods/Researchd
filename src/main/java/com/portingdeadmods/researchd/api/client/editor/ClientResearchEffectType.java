package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import org.jetbrains.annotations.Nullable;

public interface ClientResearchEffectType {
    default int getHeight() {
        return 128;
    }

    ResearchEffectType type();

    void buildLayout(RememberingLinearLayout layout, ClientResearchEffectType.Context context);

    ResearchEffect createResearchEffect(RememberingLinearLayout layout);

    static ClientResearchEffectType getClientEffectType(ResearchEffectType effectType) {
        return ResearchdClient.CLIENT_RESEARCH_EFFECT_TYPES.get(effectType.id());
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
