package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

// TODO: Change icon to List<ItemStack> and use a CycledItemRenderer
public record SimpleResearch(Item icon, ResearchMethod researchMethod, ResearchEffect researchEffect,
                             List<ResourceKey<Research>> parents, boolean requiresParent,
                             Optional<String> literalName, Optional<String> literalDescription) implements Research {
    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleResearch(
                Item icon1, ResearchMethod method, ResearchEffect effect, List<ResourceKey<Research>> parents1,
                boolean parent, Optional<String> name, Optional<String> desc
        ))) return false;
        return requiresParent == parent && Objects.equals(icon, icon1) && Objects.equals(researchMethod, method) 
                && Objects.equals(researchEffect, effect) && Objects.equals(parents, parents1)
                && Objects.equals(literalName, name) && Objects.equals(literalDescription, desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, researchMethod, researchEffect, parents, requiresParent, literalName, literalDescription);
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CodecUtils.registryCodec(BuiltInRegistries.ITEM).fieldOf("icon").forGetter(SimpleResearch::icon),
                ResearchMethod.CODEC.fieldOf("method").forGetter(SimpleResearch::researchMethod),
                ResearchEffect.CODEC.optionalFieldOf("effect", EmptyResearchEffect.INSTANCE).forGetter(SimpleResearch::researchEffect),
                Research.RESOURCE_KEY_CODEC.listOf().fieldOf("parents").forGetter(SimpleResearch::parents),
                Codec.BOOL.fieldOf("requires_parent").forGetter(SimpleResearch::requiresParent),
                Codec.STRING.optionalFieldOf("literal_name").forGetter(SimpleResearch::literalName),
                Codec.STRING.optionalFieldOf("literal_description").forGetter(SimpleResearch::literalDescription)
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
        private ResearchMethod researchMethod;
        private ResearchEffect researchEffect = EmptyResearchEffect.INSTANCE;
        private UniqueArray<ResourceKey<Research>> parents = new UniqueArray<>();
        private boolean requiresParent = false;
        private Optional<String> literalName = Optional.empty();
        private Optional<String> literalDescription = Optional.empty();

        public static Builder of() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder icon(Item icon) {
            this.icon = icon;
            return this;
        }

        public Builder researchMethod(ResearchMethod researchMethod) {
            this.researchMethod = researchMethod;
            return this;
        }

        public Builder researchEffect(ResearchEffect researchEffect) {
            this.researchEffect = researchEffect;
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

        public Builder literalName(String name) {
            this.literalName = Optional.of(name);
            return this;
        }

        public Builder literalDescription(String description) {
            this.literalDescription = Optional.of(description);
            return this;
        }

        @Override
        public SimpleResearch build() {
            return new SimpleResearch(this.icon, this.researchMethod, this.researchEffect, this.parents, this.requiresParent, this.literalName, this.literalDescription);
        }
    }
}
