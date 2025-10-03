package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SimpleResearch(ItemResearchIcon researchIcon, ResearchMethod researchMethod, ResearchEffect researchEffect,
                             List<ResourceKey<Research>> parents, boolean requiresParent,
                             Optional<String> literalName, Optional<String> literalDescription) implements Research {
    public SimpleResearch {
        Researchd.LOGGER.debug("Creating simple research");
    }

    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleResearch(
                ItemResearchIcon icon1, ResearchMethod method, ResearchEffect effect, List<ResourceKey<Research>> parents1,
                boolean parent, Optional<String> name, Optional<String> desc
        ))) return false;
        return requiresParent == parent && Objects.equals(researchIcon, icon1) && Objects.equals(researchMethod, method)
                && Objects.equals(researchEffect, effect) && Objects.equals(parents, parents1)
                && Objects.equals(literalName, name) && Objects.equals(literalDescription, desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(researchIcon, researchMethod, researchEffect, parents, requiresParent, literalName, literalDescription);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemResearchIcon.CODEC.fieldOf("icon").forGetter(SimpleResearch::researchIcon),
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

    }

    public static class Builder {
        private ItemResearchIcon icon = ItemResearchIcon.EMPTY;
        private ResearchMethod researchMethod;
        private ResearchEffect researchEffect = EmptyResearchEffect.INSTANCE;
        private final UniqueArray<ResourceKey<Research>> parents = new UniqueArray<>();
        private boolean requiresParent = false;
        private Optional<String> literalName = Optional.empty();
        private Optional<String> literalDescription = Optional.empty();

        private Builder() {
        }

        public Builder icon(Item icon) {
            this.icon = ItemResearchIcon.single(icon);
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

        public SimpleResearch build() {
            return new SimpleResearch(this.icon, this.researchMethod, this.researchEffect, this.parents, this.requiresParent, this.literalName, this.literalDescription);
        }
    }
}
