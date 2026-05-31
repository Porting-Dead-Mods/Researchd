package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
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

    public static final MapCodec<DimensionUnlockEffectData> CODEC = UniqueArray.CODEC(ResourceKey.codec(Registries.DIMENSION_TYPE))
            .xmap(DimensionUnlockEffectData::new, DimensionUnlockEffectData::blockedDimensions).fieldOf("blocked_dimensions");
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionUnlockEffectData> STREAM_CODEC = StreamCodec.composite(
            UniqueArray.STREAM_CODEC(ResourceKey.streamCodec(Registries.DIMENSION_TYPE)),
            DimensionUnlockEffectData::blockedDimensions,
            DimensionUnlockEffectData::new
    );
    public static final ResearchEffectDataType<DimensionUnlockEffectData> TYPE = ResearchEffectDataType.simple(DimensionUnlockEffectData::new, CODEC, STREAM_CODEC);

    public DimensionUnlockEffectData() {
        this(new UniqueArray<>());
    }

    @Override
    public void add(DimensionUnlockEffect predicate, Level level) {
        UniqueArray<ResourceKey<DimensionType>> dimensions = this.blockedDimensions();
        dimensions.add(predicate.getDimensionType());
    }

    @Override
    public void remove(DimensionUnlockEffect predicate, Level level) {
        UniqueArray<ResourceKey<DimensionType>> dimensions = this.blockedDimensions();
        dimensions.remove(predicate.getDimensionType());
    }

    @Override
    public UniqueArray<ResourceKey<DimensionType>> getAll() {
        return this.blockedDimensions();
    }

    @Override
    public ResearchEffectDataType<? extends ResearchEffectData<DimensionUnlockEffect>> type() {
        return TYPE;
    }

    @Override
    public void initDefault(Level level) {
        Collection<DimensionUnlockEffect> effects = ResearchHelperCommon.getResearchEffects(DimensionUnlockEffect.class, level);

        for (DimensionUnlockEffect effect : effects) {
            this.blockedDimensions.add(effect.getDimensionType());
        }

    }
}
