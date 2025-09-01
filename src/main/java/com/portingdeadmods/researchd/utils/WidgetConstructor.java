package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;

@FunctionalInterface
public interface WidgetConstructor<T> {
    AbstractResearchInfoWidget<T> create(int x, int y, T method);

    default AbstractResearchInfoWidget<? extends ResearchMethod> createMethod(int x, int y, ResearchMethod researchMethod) {
         return (AbstractResearchInfoWidget<? extends ResearchMethod>) create(x, y, (T) researchMethod);
    }

    default AbstractResearchInfoWidget<? extends ResearchEffect> createEffect(int x, int y, ResearchEffect researchEffect) {
        return (AbstractResearchInfoWidget<? extends ResearchEffect>) create(x, y, (T) researchEffect);
    }

}
