package com.portingdeadmods.researchd.impl.capabilities;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.capabilties.Research;

import java.util.Collections;
import java.util.List;

public record EntityResearchImpl(List<Research> research) implements EntityResearch {
    public static final EntityResearchImpl EMPTY = new EntityResearchImpl(Collections.emptyList());

    public static final Codec<EntityResearchImpl> CODEC = Codec.list(Research.CODEC).xmap(EntityResearchImpl::new, EntityResearchImpl::research);
}
