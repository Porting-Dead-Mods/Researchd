package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import org.jetbrains.annotations.NotNull;

public class AndResearchEffectWidget extends AbstractResearchEffectListWidget<AndResearchEffect> {
    public AndResearchEffectWidget(int x, int y, AndResearchEffect effect) {
        super(x, y, effect);
    }

    @Override
    public @NotNull String getSeparatorText() {
        return "and";
    }
}
