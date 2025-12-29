package com.portingdeadmods.researchd.api.client.editor;

import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import org.jetbrains.annotations.Nullable;

public interface ClientDataCreationContext {
    boolean isCreateButtonActive();

    void setCreateButtonActive(boolean active);

    ResearchScreen parentScreen();

    @Nullable PopupWidget parentPopupWidget();

    int widgetWidth();

    int widgetHeight();

    int innerWidth();

    int innerHeight();

    int padding();
}
