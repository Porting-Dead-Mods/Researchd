package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A simple holder for a string key for usage in the {@link ResearchTeam#getTeamEffectList()}
 * Every instance should be registered in {@link com.portingdeadmods.researchd.ResearchdRegistries#VALUE_EFFECT}, but not required.
 */
public final class ValueEffect {
    public static final Codec<ValueEffect> CODEC = Codec.STRING.xmap(ValueEffect::new, ValueEffect::get);
    public static final StreamCodec<ByteBuf, ValueEffect> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.cast().map(ValueEffect::new, ValueEffect::get);

    private final String key;

    public String get() {
        return key;
    }

    public ValueEffect(String key) {
        this.key = key;
    }
}
