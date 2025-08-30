package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.*;

public record DimensionPredicateData(Set<ResourceKey<DimensionType>> blockedDimensions) implements ResearchEffectData<DimensionPredicate> {
    public static final DimensionPredicateData EMPTY = new DimensionPredicateData(Collections.emptySet());
    public static final Codec<DimensionPredicateData> CODEC = CodecUtils.set(ResourceKey.codec(Registries.DIMENSION_TYPE)).xmap(DimensionPredicateData::new, DimensionPredicateData::blockedDimensions);

    public DimensionPredicateData add(DimensionPredicate predicate, Level level) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.add(predicate.getDimension());
        return new DimensionPredicateData(dimensions);
    }

    public DimensionPredicateData remove(DimensionPredicate predicate, Level level) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.remove(predicate.getDimension());
        return new DimensionPredicateData(dimensions);
    }

    public Set<ResourceKey<DimensionType>> getAll() {
        return this.blockedDimensions();
    }

    public DimensionPredicateData getDefault(Level level) {
        Collection<DimensionPredicate> dps = ResearchHelperCommon.getResearchEffects(DimensionPredicate.class, level);

        Set<ResourceKey<DimensionType>> blockedDimensions = new UniqueArray<>();
        for (DimensionPredicate predicate : dps) {
            blockedDimensions.add(predicate.getDimension());
        }
        return new DimensionPredicateData(blockedDimensions);
    }
}
