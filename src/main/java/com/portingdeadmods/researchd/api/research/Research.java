package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a single research
 */
public interface Research {
    Codec<Research> CODEC = ResearchdRegistries.RESEARCH_SERIALIZER.byNameCodec().dispatch(Research::getSerializer, ResearchSerializer::codec);
    Codec<ResourceKey<Research>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_KEY);

    Item icon();

    // TODO: Encode this using strings as keys
    /**
     * @return A map that maps the possible research packs that can be used and the quantity required
     */
    Map<ResourceKey<ResearchPack>, Integer> researchPoints();

    /**
     * @return An {@link Optional} {@link ResourceKey} which represents the parent of this research.
     */
    List<ResourceKey<Research>> parents();

    /**
     * @return whether the parent needs to researched to research this research
     */
    boolean requiresParent();

    ResearchSerializer<?> getSerializer();

    interface Builder<T extends Research> {
        T build();
    }
}
