package com.portingdeadmods.researchd.client.impl.methods.types;

import com.portingdeadmods.researchd.api.client.editor.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;

public class ClientCheckItemPresenceMethodType extends ClientConsumeItemResearchMethodType {
    public static final ClientCheckItemPresenceMethodType INSTANCE = new ClientCheckItemPresenceMethodType();

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CHECK_ITEM_PRESENCE.get();
    }
}
