package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record ResearchPack(int color, Optional<ResourceLocation> customTexture) {
    public static final Codec<ResearchPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(ResearchPack::color),
            ResourceLocation.CODEC.optionalFieldOf("customTexture").forGetter(ResearchPack::customTexture)
    ).apply(instance, ResearchPack::new));
    public static final Codec<ResourceKey<ResearchPack>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY);
    public static final ResearchPack EMPTY = new ResearchPack(-1, Optional.empty());

    public ResearchPack(ResourceLocation customTexture) {
        this(-1, Optional.of(customTexture));
    }

    public static final class Builder {
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

        public ResearchPack build() {
            return new ResearchPack(this.color, Optional.ofNullable(this.customTexture));
        }
    }
}
