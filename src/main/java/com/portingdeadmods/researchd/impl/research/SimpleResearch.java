package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record SimpleResearch(ResearchIcon researchIcon, ResearchMethod researchMethod, ResearchEffect researchEffect,
                             List<ResourceKey<Research>> parents, boolean requiresParent,
                             ResourceLocation researchPage, DisplayImpl display) implements Research, RegistryDisplay<Research> {
    public static final String ID = "simple";

    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ResourceLocation researchPage() {
        return this.researchPage;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Component getDisplayName(ResourceKey<Research> key) {
        return this.display.name().orElse(Research.getLangName(key));
    }

    @Override
    public Component getDisplayDescription(ResourceKey<Research> key) {
        return this.display.desc().orElse(Research.getLangDesc(key));
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResearchIcon.CODEC.fieldOf("icon").forGetter(SimpleResearch::researchIcon),
                ResearchMethod.CODEC.fieldOf("method").forGetter(SimpleResearch::researchMethod),
                ResearchEffect.CODEC.optionalFieldOf("effect", EmptyResearchEffect.INSTANCE).forGetter(SimpleResearch::researchEffect),
                Research.RESOURCE_KEY_CODEC.listOf().optionalFieldOf("parents", List.of()).forGetter(SimpleResearch::parents),
                Codec.BOOL.orElse(true).fieldOf("requires_parent").forGetter(SimpleResearch::requiresParent),
                ResourceLocation.CODEC.optionalFieldOf("research_page", ResearchPage.DEFAULT_PAGE_ID).forGetter(SimpleResearch::researchPage),
                DisplayImpl.CODEC.optionalFieldOf("display", DisplayImpl.EMPTY).forGetter(SimpleResearch::display)
        ).apply(instance, SimpleResearch::new));
        public static final StreamCodec<? super RegistryFriendlyByteBuf, SimpleResearch> STREAM_CODEC = NeoForgeStreamCodecs.composite(
                ResearchIcon.STREAM_CODEC,
                SimpleResearch::researchIcon,
                ResearchMethod.STREAM_CODEC,
                SimpleResearch::researchMethod,
                ResearchEffect.STREAM_CODEC,
                SimpleResearch::researchEffect,
                Research.RESOURCE_KEY_STREAM_CODEC.apply(ByteBufCodecs.list()),
                SimpleResearch::parents,
                ByteBufCodecs.BOOL,
                SimpleResearch::requiresParent,
                ResourceLocation.STREAM_CODEC,
                SimpleResearch::researchPage,
                DisplayImpl.STREAM_CODEC,
                SimpleResearch::display,
                SimpleResearch::new
        );

        private Serializer() {
        }

        @Override
        public MapCodec<SimpleResearch> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SimpleResearch> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder {
        private ItemResearchIcon icon = ItemResearchIcon.EMPTY;
        private ResearchMethod researchMethod;
        private ResearchEffect researchEffect = EmptyResearchEffect.INSTANCE;
        private final UniqueArray<ResourceKey<Research>> parents = new UniqueArray<>();
        private boolean requiresParent = false;
        private ResourceLocation researchPage = ResearchPage.DEFAULT_PAGE_ID;
        private Optional<Component> literalName = Optional.empty();
        private Optional<Component> literalDescription = Optional.empty();

        private Builder() {
        }

        public Builder icon(Item icon) {
            this.icon = ItemResearchIcon.single(icon);
            return this;
        }

        public Builder icon(ItemResearchIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder method(ResearchMethod researchMethod) {
            this.researchMethod = researchMethod;
            return this;
        }

        public Builder effect(ResearchEffect researchEffect) {
            this.researchEffect = researchEffect;
            return this;
        }

        @SafeVarargs
        public final Builder parents(ResourceKey<Research>... parents) {
            this.parents.addAll(List.of(parents));
            return this;
        }

        public Builder parents(Collection<ResourceKey<Research>> parents) {
            this.parents.addAll(parents);
            return this;
        }

        public Builder requiresParent(boolean requiresParent) {
            this.requiresParent = requiresParent;
            return this;
        }

        public Builder researchPage(ResourceLocation researchPage) {
            this.researchPage = researchPage;
            return this;
        }

        public Builder literalName(String name) {
            this.literalName = Optional.of(Component.literal(name));
            return this;
        }

        public Builder literalDescription(String description) {
            this.literalDescription = Optional.of(Component.literal(description));
            return this;
        }

        public Builder literalName(Component name) {
            this.literalName = Optional.of(name);
            return this;
        }

        public Builder literalDescription(Component description) {
            this.literalDescription = Optional.of(description);
            return this;
        }

        public SimpleResearch build() {
            return new SimpleResearch(this.icon, this.researchMethod, this.researchEffect, this.parents, this.requiresParent, this.researchPage, new DisplayImpl(this.literalName, this.literalDescription));
        }
    }
}
