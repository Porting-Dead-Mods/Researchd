package com.portingdeadmods.researchd.content.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPredicate;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record DimensionPredicate(ResourceKey<DimensionType> dimension) implements ResearchPredicate {
    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        DimensionPredicateData data = player.getData(ResearchdAttachments.DIMENSION_PREDICATE.get());
        player.setData(ResearchdAttachments.DIMENSION_PREDICATE.get(), data.removeBlockedDimension(this.dimension));
    }

    @Override
    public ResearchPredicateSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchPredicateSerializer<DimensionPredicate> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<DimensionPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceKey.codec(Registries.DIMENSION_TYPE).fieldOf("dimension").forGetter(DimensionPredicate::dimension)
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
