package com.portingdeadmods.researchd.api.capabilties;

import com.portingdeadmods.researchd.Researchd;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class ResearchdCapabilities {
    public static final EntityCapability<EntityResearch, Void> ENTITY = EntityCapability.createVoid(Researchd.rl("entity_research"), EntityResearch.class);
}
