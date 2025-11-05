package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import org.jetbrains.annotations.NotNull;

public class AndResearchMethodWidget extends AbstractResearchMethodListWidget<AndResearchMethod> {
    public AndResearchMethodWidget(int x, int y, AndResearchMethod method) {
        super(x, y, method);
    }

    @Override
    public @NotNull String getSeparatorText() {
        return "+";
    }
}
