package com.portingdeadmods.researchd.api.research.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.api.utils.RGBAColor;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ResearchPack(int color, int sortingValue, Optional<ResourceLocation> customTexture) {
    public static final Codec<ResearchPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RGBAColor.CODEC.fieldOf("color").forGetter(ResearchPack::colorAsRgba),
            Codec.INT.fieldOf("sortingValue").forGetter(ResearchPack::sortingValue),
            ResourceLocation.CODEC.optionalFieldOf("customTexture").forGetter(ResearchPack::customTexture)
    ).apply(instance, ResearchPack::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ResearchPack::color,
            ByteBufCodecs.INT,
            ResearchPack::sortingValue,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
            ResearchPack::customTexture,
            ResearchPack::new
    );

    public static final Codec<ResourceKey<ResearchPack>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY);
    public static final StreamCodec<ByteBuf, ResourceKey<ResearchPack>> RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_PACK_KEY);

    public static final ResearchPack EMPTY = new ResearchPack(-1, -1, java.util.Optional.empty());

    public ResearchPack(ResourceLocation customTexture) {
        this(-1, -1, Optional.of(customTexture));
    }

    public ResearchPack(RGBAColor color, int sortingValue, Optional<ResourceLocation> customTexture) {
        this(color.toARGB(), sortingValue, customTexture);
    }

    public RGBAColor colorAsRgba() {
        int red = FastColor.ARGB32.red(this.color);
        int green = FastColor.ARGB32.green(this.color);
        int blue = FastColor.ARGB32.blue(this.color);
        int alpha = FastColor.ARGB32.alpha(this.color);
        return new RGBAColor(red, green, blue, alpha);
    }

    public static ItemStack asStack(ResourceKey<ResearchPack> key) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();
        stack.set(ResearchdDataComponents.RESEARCH_PACK, new ResearchPackComponent(Optional.of(key)));
        return stack;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int color = -1;
        private int sorting_value = -1;
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

        /**
         * A value to dictate where in the progression the research pack should be. <br>
         * Lower = earlier, higher = later
         */
        public Builder sortingValue(int sorting_value) {
            this.sorting_value = sorting_value;
            return this;
        }

        public ResearchPack build() {
            return new ResearchPack(this.color, this.sorting_value, java.util.Optional.ofNullable(this.customTexture));
        }
    }
}
