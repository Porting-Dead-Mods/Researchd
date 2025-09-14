package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface ValueEffect {
    Codec<ValueEffect> CODEC = CodecUtils.registryCodec(ResearchdRegistries.VALUE_EFFECT);
    StreamCodec<? super RegistryFriendlyByteBuf, ValueEffect> STREAM_CODEC = CodecUtils.registryStreamCodec(ResearchdRegistries.VALUE_EFFECT);

    default ResourceLocation getKey() {
        return ResearchdRegistries.VALUE_EFFECT.getKey(this);
    }

    default void onUnlock(ResearchTeam team, Level level) {

    }
}
