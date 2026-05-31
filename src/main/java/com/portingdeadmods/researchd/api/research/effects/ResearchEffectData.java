package com.portingdeadmods.researchd.api.research.effects;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public interface ResearchEffectData<T extends ResearchEffect> {
    Codec<ResearchEffectData<?>> CODEC = ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE.byNameCodec().dispatch(ResearchEffectData::type, ResearchEffectDataType::codec);
    StreamCodec<? super RegistryFriendlyByteBuf, ResearchEffectData<?>> STREAM_CODEC = ByteBufCodecs.registry(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE_KEY).dispatch(ResearchEffectData::type, ResearchEffectDataType::streamCodec);

    /**
     * This method should return a 'default' instance of the Data class
     * As a default, it should hold all the effects as if they weren't researched yet.
     */
    void initDefault(Level level);

    // Storage methods
    void add(T effect, Level level);

    void remove(T effect, Level level);

    UniqueArray<?> getAll();

    ResearchEffectDataType<? extends ResearchEffectData<T>> type();

    static <T extends ResearchEffectData<?>> T create(Supplier<T> factory, Level level) {
        T data = factory.get();
        data.initDefault(level);
        return data;
    }

}
