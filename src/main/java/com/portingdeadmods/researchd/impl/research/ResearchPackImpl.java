package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.api.misc.RGBAColor;
import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPackSerializer;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import java.util.Optional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record ResearchPackImpl(int color, int sortingValue, Optional<Identifier> customTexture, DisplayImpl display)
        implements ResearchPack, RegistryDisplay<ResearchPack> {

    public static final ResearchPackImpl EMPTY = new ResearchPackImpl(-1, -1, Optional.empty(), DisplayImpl.EMPTY);
    public static final String ID = "simple";

    public ResearchPackImpl(Identifier customTexture) {
        this(-1, -1, Optional.of(customTexture), DisplayImpl.EMPTY);
    }

    public ResearchPackImpl(
            RGBAColor color, int sortingValue, Optional<Identifier> customTexture, DisplayImpl display) {
        this(color.toARGB(), sortingValue, customTexture, display);
    }

    public RGBAColor colorAsRgba() {
        int red = ARGB.red(this.color);
        int green = ARGB.green(this.color);
        int blue = ARGB.blue(this.color);
        int alpha = ARGB.alpha(this.color);
        return new RGBAColor(red, green, blue, alpha);
    }

    public static ItemStack asStack(ResourceKey<ResearchPack> key) {
        return asTemplate(key).create();
    }

    // Safe before item components are bound, unlike asStack; the default datapack is built that early
    public static ItemStackTemplate asTemplate(ResourceKey<ResearchPack> key) {
        return new ItemStackTemplate(
                ResearchdItems.RESEARCH_PACK.get(),
                DataComponentPatch.builder()
                        .set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(key)))
                        .build());
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
                        Identifier.CODEC.optionalFieldOf("custom_texture").forGetter(ResearchPackImpl::customTexture),
                        DisplayImpl.CODEC
                                .optionalFieldOf("display", DisplayImpl.EMPTY)
                                .forGetter(ResearchPackImpl::display))
                .apply(instance, ResearchPackImpl::new));
        public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchPackImpl> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT,
                        ResearchPackImpl::color,
                        ByteBufCodecs.INT,
                        ResearchPackImpl::sortingValue,
                        ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                        ResearchPackImpl::customTexture,
                        DisplayImpl.STREAM_CODEC,
                        ResearchPackImpl::display,
                        ResearchPackImpl::new);

        private Serializer() {}

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
        private Identifier customTexture;
        private Component literalName;
        private Component literalDescription;

        private Builder() {}

        public Builder color(int r, int g, int b) {
            this.color = ARGB.color(r, g, b);
            return this;
        }

        public Builder customTexture(Identifier customTexture) {
            this.customTexture = customTexture;
            return this;
        }

        /**
         * A value to dictate where in the progression the researchPack pack should be. <br>
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
            return new ResearchPackImpl(
                    this.color,
                    this.sorting_value,
                    Optional.ofNullable(this.customTexture),
                    new DisplayImpl(
                            Optional.ofNullable(this.literalName), Optional.ofNullable(this.literalDescription)));
        }
    }
}
