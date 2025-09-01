package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import org.jetbrains.annotations.NotNull;

public class OrResearchMethodWidget extends AbstractResearchMethodListWidget<OrResearchMethod> {
    public OrResearchMethodWidget(int x, int y, OrResearchMethod method) {
        super(x, y, method);
    }

    @Override
    public @NotNull String getSeparatorText() {
        return "or";
    }
}
