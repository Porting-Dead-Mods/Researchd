package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.client.research.ClientResearchEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record DimensionPredicate(ResourceLocation dimension) implements ResearchEffect {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        DimensionPredicateData data = player.getData(ResearchdAttachments.DIMENSION_PREDICATE.get());
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
    public ClientResearchEffect<?> getClientResearchEffect() {
        return null;
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchEffectSerializer<DimensionPredicate> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<DimensionPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(DimensionPredicate::dimension)
        ).apply(instance, DimensionPredicate::new));

        private Serializer() {
        }

        @Override
        public MapCodec<DimensionPredicate> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DimensionPredicate> streamCodec() {
            return null;
        }
    }
}
