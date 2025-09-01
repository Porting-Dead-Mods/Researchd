package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record DimensionUnlockEffectData(Set<ResourceKey<DimensionType>> blockedDimensions) implements ResearchEffectData<DimensionUnlockEffect> {
    public static final DimensionUnlockEffectData EMPTY = new DimensionUnlockEffectData(Collections.emptySet());
    public static final Codec<DimensionUnlockEffectData> CODEC = CodecUtils.set(ResourceKey.codec(Registries.DIMENSION_TYPE)).xmap(DimensionUnlockEffectData::new, DimensionUnlockEffectData::blockedDimensions);

    public DimensionUnlockEffectData add(DimensionUnlockEffect predicate, Level level) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.add(predicate.getDimensionType());
        return new DimensionUnlockEffectData(dimensions);
    }

    public DimensionUnlockEffectData remove(DimensionUnlockEffect predicate, Level level) {
        Set<ResourceKey<DimensionType>> dimensions = new HashSet<>(this.blockedDimensions());
        dimensions.remove(predicate.getDimensionType());
        return new DimensionUnlockEffectData(dimensions);
    }

    public Set<ResourceKey<DimensionType>> getAll() {
        return this.blockedDimensions();
    }

    public DimensionUnlockEffectData getDefault(Level level) {
        Collection<DimensionUnlockEffect> dps = ResearchHelperCommon.getResearchEffects(DimensionUnlockEffect.class, level);

        Set<ResourceKey<DimensionType>> blockedDimensions = new UniqueArray<>();
        for (DimensionUnlockEffect predicate : dps) {
            blockedDimensions.add(predicate.getDimensionType());
        }
        return new DimensionUnlockEffectData(blockedDimensions);
    }
}
