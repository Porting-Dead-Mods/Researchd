package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;

// TODO: Change icon to Ingredient
public record SimpleResearch(Item icon, ResearchMethod researchMethod, List<ResearchEffect> researchEffects,
                             List<ResourceKey<Research>> parents, boolean requiresParent) implements Research {
    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleResearch that)) return false;
        return requiresParent == that.requiresParent && Objects.equals(icon, that.icon) && Objects.equals(parents, that.parents) && Objects.equals(researchMethod, that.researchMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, researchMethod, parents, requiresParent);
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CodecUtils.registryCodec(BuiltInRegistries.ITEM).fieldOf("icon").forGetter(SimpleResearch::icon),
                ResearchMethod.CODEC.fieldOf("research_methods").forGetter(SimpleResearch::researchMethod),
                ResearchEffect.CODEC.listOf().fieldOf("research_effects").forGetter(SimpleResearch::researchEffects),
                Research.RESOURCE_KEY_CODEC.listOf().fieldOf("parents").forGetter(SimpleResearch::parents),
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

    public static class Builder implements Research.Builder<SimpleResearch> {
        private Item icon = Items.AIR;
        private ResearchMethod researchMethods;
        private UniqueArray<ResearchEffect> researchEffects = new UniqueArray<>();
        private UniqueArray<ResourceKey<Research>> parents = new UniqueArray<>();
        private boolean requiresParent = false;

        public static Builder of() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder icon(Item icon) {
            this.icon = icon;
            return this;
        }

        public Builder researchMethods(ResearchMethod researchMethods) {
            this.researchMethods = researchMethods;
            return this;
        }

        public Builder researchEffects(ResearchEffect... researchEffects) {
            this.researchEffects.addAll(List.of(researchEffects));
            return this;
        }

        public Builder researchEffects(List<? extends ResearchEffect> researchEffects) {
            this.researchEffects.addAll(researchEffects);
            return this;
        }

        @SafeVarargs
        public final Builder parents(ResourceKey<Research>... parents) {
            this.parents.addAll(List.of(parents));
            return this;
        }

        public Builder requiresParent(boolean requiresParent) {
            this.requiresParent = requiresParent;
            return this;
        }

        @Override
        public SimpleResearch build() {
            return new SimpleResearch(this.icon, this.researchMethods, this.researchEffects, this.parents, this.requiresParent);
        }
    }
}
