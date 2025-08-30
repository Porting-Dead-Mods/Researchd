package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;

@FunctionalInterface
public interface WidgetConstructor<T> {
    AbstractResearchInfoWidget<T> create(int x, int y, T method);

    default AbstractResearchInfoWidget<? extends ResearchMethod> create(ResearchMethod researchMethod, int x, int y) {
         return (AbstractResearchInfoWidget<? extends ResearchMethod>) create(x, y, (T) researchMethod);
    }
}
