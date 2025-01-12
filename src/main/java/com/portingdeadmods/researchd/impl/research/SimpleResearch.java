package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import com.portingdeadmods.researchd.api.research.serializers.ResearchSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: Change icon to Ingredient
public record SimpleResearch(Item icon, Map<ResourceKey<ResearchPack>, Integer> researchPoints, Optional<ResourceKey<Research>> parent, boolean requiresParent) implements Research {
    public static SimpleResearch debug(ItemLike icon) {
        return new SimpleResearch(icon.asItem(), Map.of(), Optional.empty(), false);
    }

    private static SimpleResearch fromStringMap(Item icon, Map<String, Integer> researchPoints, Optional<ResourceKey<Research>> parent, boolean requiresParent) {
        Map<ResourceKey<ResearchPack>, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> researchPoint : researchPoints.entrySet()) {
            ResourceKey<ResearchPack> researchPack = ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(researchPoint.getKey()));
            map.put(researchPack, researchPoint.getValue());
        }
        return new SimpleResearch(icon, map, parent, requiresParent);
    }

    private Map<String, Integer> getStringMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<ResourceKey<ResearchPack>, Integer> researchPoint : this.researchPoints.entrySet()) {
            map.put(researchPoint.getKey().location().toString(), researchPoint.getValue());
        }
        return map;
    }

    @Override
    public ResearchSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements ResearchSerializer<SimpleResearch> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<SimpleResearch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CodecUtils.registryCodec(BuiltInRegistries.ITEM).fieldOf("icon").forGetter(SimpleResearch::icon),
                Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("research_points").forGetter(SimpleResearch::getStringMap),
                ExtraCodecs.optionalEmptyMap(Research.RESOURCE_KEY_CODEC).fieldOf("parent").forGetter(SimpleResearch::parent),
                Codec.BOOL.fieldOf("requires_parent").forGetter(SimpleResearch::requiresParent)
        ).apply(instance, SimpleResearch::fromStringMap));

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
        private Map<ResourceKey<ResearchPack>, Integer> researchPacks = Map.of();
        private ResourceKey<Research> parent = null;
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

        public Builder researchPacks(Map<ResourceKey<ResearchPack>, Integer> researchPacks) {
            this.researchPacks = researchPacks;
            return this;
        }

        public Builder parent(@Nullable ResourceKey<Research> parent) {
            this.parent = parent;
            return this;
        }

        public Builder requiresParent(boolean requiresParent) {
            this.requiresParent = requiresParent;
            return this;
        }

        @Override
        public SimpleResearch build() {
            return new SimpleResearch(this.icon, this.researchPacks, Optional.ofNullable(this.parent), this.requiresParent);
        }
    }
}
