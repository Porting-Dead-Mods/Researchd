package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record DimensionUnlockEffect(ResourceLocation dimension) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        DimensionUnlockEffectData data = player.getData(ResearchdAttachments.DIMENSION_PREDICATE.get());
        player.setData(ResearchdAttachments.DIMENSION_PREDICATE.get(), data.remove(this, level));
    }

    @Override
    public ResourceLocation id() {
        return null;
    }

    public ResourceKey<DimensionType> getDimension() {
        return ResourceKey.create(Registries.DIMENSION_TYPE, this.dimension());
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<DimensionUnlockEffect> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<DimensionUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(DimensionUnlockEffect::dimension)
        ).apply(instance, DimensionUnlockEffect::new));

        private Serializer() {
        }

        @Override
        public MapCodec<DimensionUnlockEffect> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DimensionUnlockEffect> streamCodec() {
            return null;
        }
    }
}
