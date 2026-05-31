package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record DimensionUnlockEffect(ResourceLocation dimension,
                                    ResourceLocation dimensionIconSprite) implements ResearchEffect {
    public static final ResourceLocation ID = Researchd.rl("dimension_unlock");

    public static final ResourceLocation DEFAULT_SPRITE = Researchd.rl("dimension_icons/default");
    public static final ResourceLocation OVERWORLD_SPRITE = Researchd.rl("dimension_icons/overworld");
    public static final ResourceLocation NETHER_SPRITE = Researchd.rl("dimension_icons/nether");
    public static final ResourceLocation END_SPRITE = Researchd.rl("dimension_icons/end");

    private static final MapCodec<DimensionUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(DimensionUnlockEffect::dimension),
            ResourceLocation.CODEC.optionalFieldOf("icon_sprite", DEFAULT_SPRITE).forGetter(DimensionUnlockEffect::dimensionIconSprite)
    ).apply(instance, DimensionUnlockEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DimensionUnlockEffect> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            DimensionUnlockEffect::dimension,
            ResourceLocation.STREAM_CODEC,
            DimensionUnlockEffect::dimensionIconSprite,
            DimensionUnlockEffect::new
    );

    public static final ResearchEffectSerializer<DimensionUnlockEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);

    @Override
    public void onUnlock(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (!level.isClientSide()) {
            TeamResearchEffectDataMap map = TeamResearchEffectSavedData.getData((ServerLevel) level);
            DimensionUnlockEffectData data = map.computeIfAbsent(team.getId(), ResearchdEffectDataTypes.DIMENSION_UNLOCK, level);
            data.remove(this, level);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.DIMENSION_UNLOCK.get();
    }

    public ResourceKey<DimensionType> getDimensionType() {
        return ResourceKey.create(Registries.DIMENSION_TYPE, this.dimension());
    }

    public ResourceKey<Level> getDimension() {
        return ResourceKey.create(Registries.DIMENSION, this.dimension());
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
