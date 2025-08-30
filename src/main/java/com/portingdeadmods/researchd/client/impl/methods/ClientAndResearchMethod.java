package com.portingdeadmods.researchd.client.impl.methods;

import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import org.jetbrains.annotations.NotNull;

public class ClientAndResearchMethod extends AbstractResearchMethodList<AndResearchMethod> {
    public static final ClientAndResearchMethod INSTANCE = new ClientAndResearchMethod();

    @Override
    public @NotNull String getSeparatorText() {
        return "and";
    }

}
