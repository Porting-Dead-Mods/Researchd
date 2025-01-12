package com.portingdeadmods.researchd.impl.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.research.Research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record EntityResearchImpl(List<Research> researchQueue, List<Research> research) implements EntityResearch {
    public static final EntityResearchImpl EMPTY = new EntityResearchImpl(Collections.emptyList(), Collections.emptyList());

    public static final Codec<EntityResearchImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Research.CODEC).fieldOf("researchQueue").forGetter(EntityResearchImpl::researchQueue),
            Codec.list(Research.CODEC).fieldOf("research").forGetter(EntityResearchImpl::research)
    ).apply(instance, EntityResearchImpl::new));

    public EntityResearchImpl() {
        this(new ArrayList<>(), new ArrayList<>());
    }
}
