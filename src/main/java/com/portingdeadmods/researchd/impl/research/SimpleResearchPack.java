package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record SimpleResearchPack(int color, Optional<ResourceLocation> customTexture) implements ResearchPack {
    public SimpleResearchPack(ResourceLocation customTexture) {
        this(-1, Optional.of(customTexture));
    }

    @Override
    public ResearchPackSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchPackSerializer<SimpleResearchPack> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearchPack> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("color").forGetter(SimpleResearchPack::color),
                ExtraCodecs.optionalEmptyMap(ResourceLocation.CODEC).fieldOf("customTexture").forGetter(SimpleResearchPack::customTexture)
        ).apply(instance, SimpleResearchPack::new));

        private Serializer() {
        }

        @Override
        public MapCodec<SimpleResearchPack> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SimpleResearchPack> streamCodec() {
            return null;
        }
    }

    public static final class Builder implements ResearchPack.Builder<SimpleResearchPack> {
        private int color = -1;
        private ResourceLocation customTexture;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder customTexture(ResourceLocation customTexture) {
            this.customTexture = customTexture;
            return this;
        }

        @Override
        public SimpleResearchPack build() {
            return new SimpleResearchPack(this.color, Optional.ofNullable(this.customTexture));
        }
    }
}
