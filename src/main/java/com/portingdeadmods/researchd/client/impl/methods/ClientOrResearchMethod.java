package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import org.jetbrains.annotations.NotNull;

public class ClientOrResearchMethod extends AbstractResearchMethodList<OrResearchMethod> {
    public static final ClientOrResearchMethod INSTANCE = new ClientOrResearchMethod();

    @Override
    public @NotNull String getSeparatorText() {
        return "and";
    }
}
