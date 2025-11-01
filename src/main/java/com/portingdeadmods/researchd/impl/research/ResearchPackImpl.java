package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.api.utils.RGBAColor;
import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ResearchPackImpl(int color, int sortingValue, Optional<ResourceLocation> customTexture,
                               DisplayImpl display) implements ResearchPack, RegistryDisplay<ResearchPack> {

    public static final ResearchPackImpl EMPTY = new ResearchPackImpl(-1, -1, Optional.empty(), DisplayImpl.EMPTY);

    public ResearchPackImpl(ResourceLocation customTexture) {
        this(-1, -1, Optional.of(customTexture), DisplayImpl.EMPTY);
    }

    public ResearchPackImpl(RGBAColor color, int sortingValue, Optional<ResourceLocation> customTexture, DisplayImpl display) {
        this(color.toARGB(), sortingValue, customTexture, display);
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

    @Override
    public Component getDisplayName(ResourceKey<ResearchPack> key) {
        return this.display.name().orElse(ResearchPack.getLangName(key));
    }

    @Override
    public Component getDisplayDescription(ResourceKey<ResearchPack> key) {
        return this.display.desc().orElse(ResearchPack.getLangDesc(key));
    }

    @Override
    public ResearchPackSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final class Serializer implements ResearchPackSerializer<ResearchPackImpl> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<ResearchPackImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RGBAColor.CODEC.fieldOf("color").forGetter(ResearchPackImpl::colorAsRgba),
                Codec.INT.fieldOf("sorting_value").forGetter(ResearchPackImpl::sortingValue),
                ResourceLocation.CODEC.optionalFieldOf("custom_texture").forGetter(ResearchPackImpl::customTexture),
                DisplayImpl.CODEC.optionalFieldOf("display", DisplayImpl.EMPTY).forGetter(ResearchPackImpl::display)
        ).apply(instance, ResearchPackImpl::new));
        public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchPackImpl> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                ResearchPackImpl::color,
                ByteBufCodecs.INT,
                ResearchPackImpl::sortingValue,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
                ResearchPackImpl::customTexture,
                DisplayImpl.STREAM_CODEC,
                ResearchPackImpl::display,
                ResearchPackImpl::new
        );

        private Serializer() {
        }

        @Override
        public MapCodec<ResearchPackImpl> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ResearchPackImpl> streamCodec() {
            return STREAM_CODEC;
        }

    }

    public static final class Builder {
        private int color = -1;
        private int sorting_value = -1;
        private ResourceLocation customTexture;
        private Component literalName;
        private Component literalDescription;

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
        public Builder sortingValue(int sortingValue) {
            this.sorting_value = sortingValue;
            return this;
        }

        public Builder literalName(String name) {
            this.literalName = Component.literal(name);
            return this;
        }

        public Builder literDescription(String description) {
            this.literalDescription = Component.literal(description);
            return this;
        }

        public ResearchPackImpl build() {
            return new ResearchPackImpl(this.color, this.sorting_value, Optional.ofNullable(this.customTexture), new DisplayImpl(Optional.ofNullable(this.literalName), Optional.ofNullable(this.literalDescription)));
        }
    }
}
