package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Collection;

public record DimensionUnlockEffectData(UniqueArray<ResourceKey<DimensionType>> blockedDimensions) implements ResearchEffectData<DimensionUnlockEffect> {
    public static final DimensionUnlockEffectData EMPTY = new DimensionUnlockEffectData(new UniqueArray<>());

    public static final Codec<DimensionUnlockEffectData> CODEC = UniqueArray.CODEC(ResourceKey.codec(Registries.DIMENSION_TYPE))
            .xmap(DimensionUnlockEffectData::new, DimensionUnlockEffectData::blockedDimensions);
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionUnlockEffectData> STREAM_CODEC = StreamCodec.composite(
            UniqueArray.STREAM_CODEC(ResourceKey.streamCodec(Registries.DIMENSION_TYPE)),
            DimensionUnlockEffectData::blockedDimensions,
            DimensionUnlockEffectData::new
    );

    @Override
    public DimensionUnlockEffectData add(DimensionUnlockEffect predicate, Level level) {
        UniqueArray<ResourceKey<DimensionType>> dimensions = new UniqueArray<>(this.blockedDimensions());
        dimensions.add(predicate.getDimensionType());
        return new DimensionUnlockEffectData(dimensions);
    }

    @Override
    public DimensionUnlockEffectData remove(DimensionUnlockEffect predicate, Level level) {
        UniqueArray<ResourceKey<DimensionType>> dimensions = new UniqueArray<>(this.blockedDimensions());
        dimensions.remove(predicate.getDimensionType());
        return new DimensionUnlockEffectData(dimensions);
    }

    @Override
    public UniqueArray<ResourceKey<DimensionType>> getAll() {
        return this.blockedDimensions();
    }

    @Override
    public DimensionUnlockEffectData getDefault(Level level) {
        Collection<DimensionUnlockEffect> dps = ResearchHelperCommon.getResearchEffects(DimensionUnlockEffect.class, level);
        UniqueArray<ResourceKey<DimensionType>> blocked = new UniqueArray<>();

        for (DimensionUnlockEffect predicate : dps) {
            blocked.add(predicate.getDimensionType());
        }

        return new DimensionUnlockEffectData(blocked);
    }
}
