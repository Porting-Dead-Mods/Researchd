package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a single research
 */
public interface Research {
    Codec<Research> CODEC = /* TODO */ null;
    Codec<ResourceKey<Research>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_KEY);

    /**
     * @return A map that maps the possible research packs that can be used and the quantity required
     */
    Map<ResourceKey<ResearchPack>, Integer> researchPoints();

    /**
     * @return An {@link Optional} {@link ResourceKey} which represents the parent of this research.
     */
    Optional<ResourceKey<Research>> parent();

    /**
     * @return whether the parent needs to researched to research this research
     */
    boolean requiresParent();
}
