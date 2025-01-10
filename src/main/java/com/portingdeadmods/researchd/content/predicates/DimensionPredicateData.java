package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.*;

public record DimensionPredicateData(Set<ResourceKey<DimensionType>> blockedDimensions) {
    public static final DimensionPredicateData EMPTY = new DimensionPredicateData(Collections.emptySet());
    public static final Codec<DimensionPredicateData> CODEC = CodecUtils.set(ResourceKey.codec(Registries.DIMENSION_TYPE)).xmap(DimensionPredicateData::new, DimensionPredicateData::blockedDimensions);

    public DimensionPredicateData addBlockedDimension(ResourceKey<DimensionType> dimension) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.add(dimension);
        return new DimensionPredicateData(dimensions);
    }

    public DimensionPredicateData removeBlockedDimension(ResourceKey<DimensionType> dimension) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.remove(dimension);
        return new DimensionPredicateData(dimensions);
    }
}
