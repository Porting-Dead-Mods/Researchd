package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import org.jetbrains.annotations.NotNull;

public class ClientAndResearchMethod extends AbstractResearchMethodList<AndResearchMethod> {
    public ClientAndResearchMethod(int x, int y, AndResearchMethod method) {
        super(x, y, method);
    }

    @Override
    public @NotNull String getSeparatorText() {
        return "and";
    }

}
