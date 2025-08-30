package com.portingdeadmods.researchd.api.research.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.ResearchdRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.Optional;

public record SimpleResearchPack(int color, Optional<ResourceLocation> customTexture) {
    public static final Codec<SimpleResearchPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(SimpleResearchPack::color),
            ResourceLocation.CODEC.optionalFieldOf("customTexture").forGetter(SimpleResearchPack::customTexture)
    ).apply(instance, SimpleResearchPack::new));
    public static final Codec<ResourceKey<SimpleResearchPack>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY);
    public static final StreamCodec<ByteBuf, ResourceKey<SimpleResearchPack>> RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_PACK_KEY);
    public static final SimpleResearchPack EMPTY = new SimpleResearchPack(-1, Optional.empty());

    public SimpleResearchPack(ResourceLocation customTexture) {
        this(-1, Optional.of(customTexture));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int color = -1;
        private ResourceLocation customTexture;

        private Builder() {
        }

        public Builder color(int r, int g, int b) {
            this.color = FastColor.ARGB32.color(r, g, b);
            return this;
        }

        public Builder customTexture(ResourceLocation customTexture) {
            this.customTexture = customTexture;
            return this;
        }

        public SimpleResearchPack build() {
            return new SimpleResearchPack(this.color, Optional.ofNullable(this.customTexture));
        }
    }
}
