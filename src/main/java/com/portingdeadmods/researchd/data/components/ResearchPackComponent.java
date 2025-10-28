package com.portingdeadmods.researchd.data.components;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;
import java.util.Optional;

public record ResearchPackComponent(Optional<ResourceKey<ResearchPackImpl>> researchPackKey) {
    public static final ResearchPackComponent EMPTY = new ResearchPackComponent(Optional.empty());
    public static final Codec<ResearchPackComponent> CODEC = ExtraCodecs.optionalEmptyMap(ResearchPackImpl.RESOURCE_KEY_CODEC).xmap(ResearchPackComponent::new, ResearchPackComponent::researchPackKey);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchPackComponent> STREAM_CODEC = ByteBufCodecs.optional(ResearchPackImpl.RESOURCE_KEY_STREAM_CODEC).map(ResearchPackComponent::new, ResearchPackComponent::researchPackKey);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResearchPackComponent(Optional<ResourceKey<ResearchPackImpl>> packKey))) return false;
        return Objects.equals(researchPackKey, packKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(researchPackKey);
    }
}
