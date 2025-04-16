package com.portingdeadmods.researchd.api.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Supplier;

// TODO: This should be moved to pdl
public final class PDLSavedData<T> {
    private final Supplier<T> defaultValueSupplier;
    private final Codec<T> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    private PDLSavedData(Builder<T> builder) {
        this.codec = builder.codec;
        this.streamCodec = builder.streamCodec;
        this.defaultValueSupplier = builder.defaultValueSupplier;
    }

    public Codec<T> codec() {
        return codec;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public Supplier<T> defaultValueSupplier() {
        return defaultValueSupplier;
    }

    public void setData(ServerLevel serverLevel, T data) {
        ResourceLocation location = ResearchdRegistries.SAVED_DATA.getKey(this);
        if (location != null) {
            SavedDataWrapper.setData(new SavedDataHolder<>(location, this), serverLevel, data);
        }
    }

    public T getData(ServerLevel serverLevel) {
        ResourceLocation location = ResearchdRegistries.SAVED_DATA.getKey(this);
        if (location != null) {
            return SavedDataWrapper.getData(new SavedDataHolder<>(location, this), serverLevel);
        }
        return null;
    }

    public static <T> Builder<T> builder(Codec<T> codec, Supplier<T> defaultValueSupplier) {
        return new Builder<>(codec, defaultValueSupplier);
    }

    public static final class Builder<T> {
        private final Supplier<T> defaultValueSupplier;
        private final Codec<T> codec;
        private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

        private Builder(Codec<T> codec, Supplier<T> defaultValueSupplier) {
            this.defaultValueSupplier = defaultValueSupplier;
            this.codec = codec;
        }

        public Builder<T> synced(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.streamCodec = streamCodec;
            return this;
        }

        public PDLSavedData<T> build() {
            return new PDLSavedData<>(this);
        }

    }
}
