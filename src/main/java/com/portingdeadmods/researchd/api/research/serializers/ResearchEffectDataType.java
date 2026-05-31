package com.portingdeadmods.researchd.api.research.serializers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public interface ResearchEffectDataType<T extends ResearchEffectData<?>> {
    T create();

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    static <T extends ResearchEffectData<?>> ResearchEffectDataType<T> simple(Supplier<T> factory, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return new Simple<>(factory, codec, streamCodec);
    }

    static <T extends ResearchEffectData<?>> ResearchEffectDataType<T> simple(Supplier<T> factory, MapCodec<T> codec) {
        return new Simple<>(factory, codec, ByteBufCodecs.fromCodecWithRegistries(codec.codec()));
    }

    record Simple<T extends ResearchEffectData<?>>(Supplier<T> factory, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) implements ResearchEffectDataType<T> {
        @Override
        public T create() {
            return this.factory.get();
        }
    }
}
