package com.portingdeadmods.researchd.api.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: This should be moved to pdl
// TODO: Migration codecs
public final class PDLSavedData<T> {
    private final Supplier<T> defaultValueSupplier;
    private final Codec<T> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
    private final Consumer<Player> onSyncFunction;
    private final boolean global;

    private PDLSavedData(Builder<T> builder) {
        this.codec = builder.codec;
        this.streamCodec = builder.streamCodec;
        this.defaultValueSupplier = builder.defaultValueSupplier;
        this.onSyncFunction = builder.onSyncFunction;
        this.global = builder.global;
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

    public Consumer<Player> onSyncFunction() {
        return onSyncFunction;
    }

    // TODO: FIXME
    public boolean isGlobal() {
        return false;
    }

    public boolean isSynced() {
        return streamCodec != null;
    }

    public void setData(Level level, T data) {
        if (level instanceof ServerLevel serverLevel0) {
            ServerLevel serverLevel;
            if (isGlobal()) {
                serverLevel = serverLevel0.getServer().overworld();
            } else {
                serverLevel = serverLevel0;
            }
            ResourceLocation location = ResearchdRegistries.SAVED_DATA.getKey(this);
            if (location != null) {
                SavedDataWrapper.setData(new SavedDataHolder<>(location, this), serverLevel, data);
            }
        } else {
            ResourceLocation key = ResearchdRegistries.SAVED_DATA.getKey(this);
            if (key != null){
                PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.put(key, data);
            }
        }
    }

    public T getData(Level level) {
        if (level instanceof ServerLevel serverLevel0) {
            ServerLevel serverLevel;
            if (isGlobal()) {
                serverLevel = serverLevel0.getServer().overworld();
            } else {
                serverLevel = serverLevel0;
            }
            ResourceLocation location = ResearchdRegistries.SAVED_DATA.getKey(this);
            if (location != null) {
                return SavedDataWrapper.getData(new SavedDataHolder<>(location, this), serverLevel);
            }
        } else {
            ResourceLocation location = ResearchdRegistries.SAVED_DATA.getKey(this);
            if (location != null) {
                return (T) PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.get(location);
            }
        }
        return null;
    }

    public static <T> Builder<T> builder(Codec<T> codec, Supplier<T> defaultValueSupplier) {
        return new Builder<>(codec, defaultValueSupplier);
    }

    public static final class Builder<T> {
        private final Supplier<T> defaultValueSupplier;
        private final Codec<T> codec;
        private Consumer<Player> onSyncFunction;
        private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
        private boolean global = false;

        private Builder(Codec<T> codec, Supplier<T> defaultValueSupplier) {
            this.defaultValueSupplier = defaultValueSupplier;
            this.codec = codec;
        }

        public Builder<T> synced(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.streamCodec = streamCodec;
            return this;
        }

        /**
         * The supplied method gets ran after saved data was synced.
         * This method gets executed on both sides
         */
        public Builder<T> onSync(Consumer<Player> onSyncFunction) {
            this.onSyncFunction = onSyncFunction;
            return this;
        }

        /**
         * Makes the saved data global, instead of dimension based
         */
        public Builder<T> global() {
            this.global = true;
            return this;
        }

        public PDLSavedData<T> build() {
            if (this.onSyncFunction != null && this.streamCodec == null) {
                throw new RuntimeException("Cannot provide a onSync function for a saved data without a stream coded");
            }

            if (this.streamCodec != null && this.onSyncFunction == null) {
                this.onSyncFunction = p -> {};
            }

            return new PDLSavedData<>(this);
        }

    }
}
