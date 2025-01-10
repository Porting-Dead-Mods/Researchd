package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Map;
import java.util.Optional;

public record SimpleResearch(Map<ResourceKey<ResearchPack>, Integer> researchPoints, Optional<ResourceKey<Research>> parent, boolean requiresParent) implements Research {
    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.unboundedMap(ResearchPack.RESOURCE_KEY_CODEC, Codec.INT).fieldOf("research_points").forGetter(SimpleResearch::researchPoints),
                ExtraCodecs.optionalEmptyMap(Research.RESOURCE_KEY_CODEC).fieldOf("parent").forGetter(SimpleResearch::parent),
                Codec.BOOL.fieldOf("requires_parent").forGetter(SimpleResearch::requiresParent)
        ).apply(instance, SimpleResearch::new));

        private Serializer() {
        }

        @Override
        public MapCodec<SimpleResearch> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SimpleResearch> streamCodec() {
            return null;
        }
    }
}
