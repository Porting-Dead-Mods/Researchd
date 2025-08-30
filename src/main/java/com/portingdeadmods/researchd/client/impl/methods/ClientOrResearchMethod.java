package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import org.jetbrains.annotations.NotNull;

public class ClientOrResearchMethod extends AbstractResearchMethodList<OrResearchMethod> {
    public ClientOrResearchMethod(int x, int y, OrResearchMethod method) {
        super(x, y, method);
    }

    @Override
    public @NotNull String getSeparatorText() {
        return "or";
    }
}
