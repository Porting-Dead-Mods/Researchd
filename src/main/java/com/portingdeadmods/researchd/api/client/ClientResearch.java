package com.portingdeadmods.researchd.api.client;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import org.jetbrains.annotations.Nullable;

public interface ClientResearch {
    void buildLayout(RememberingLinearLayout layout, Context context);

    Research createResearch(RememberingLinearLayout layout);

    record Context(ResearchScreen parentScreen, @Nullable PopupWidget parentPopupWidget, int widgetWidth,
                   int widgetHeight, int innerWidth, int innerHeight, int padding) {

    }

}
