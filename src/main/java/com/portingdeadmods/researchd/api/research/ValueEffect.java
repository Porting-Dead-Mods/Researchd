package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ValueEffect {
    public static final Codec<ValueEffect> CODEC = ResourceLocation.CODEC.xmap(ValueEffect::new, ValueEffect::getKey);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ValueEffect> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(ValueEffect::new, ValueEffect::getKey);
    private final ResourceLocation key;

    public ValueEffect(ResourceLocation key) {
        this.key = key;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public String getKeyAsString() {
        return this.key.toString();
    }

    public void onUnlock(ResearchTeam team, Level level) {

    }
}
