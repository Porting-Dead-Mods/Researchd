package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.PDLClientSavedData;
import com.portingdeadmods.researchd.api.data.PDLSavedData;
import com.portingdeadmods.researchd.api.data.SavedDataHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncSavedDataPayload<T>(SavedDataHolder<T> holder, T value) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return type(holder);
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.put(holder.key(), value);
            holder.value().onSyncFunction().accept(context.player());
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle SyncSavedDataPayload", err);
            return null;
        });
    }

    public static <T> Type<SyncSavedDataPayload<T>> type(SavedDataHolder<T> dataHolder) {
        return new Type<>(dataHolder.key().withPrefix("sync_").withSuffix("_payload"));
    }

    private static <T> SyncSavedDataPayload<T> untyped(SavedDataHolder<?> network, T value) {
        return new SyncSavedDataPayload<>((SavedDataHolder<T>) network, value);
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, SyncSavedDataPayload<T>> streamCodec(SavedDataHolder<?> dataHolder) {
        return StreamCodec.composite(
                SavedDataHolder.STREAM_CODEC,
                SyncSavedDataPayload::holder,
                ((SavedDataHolder<T>) dataHolder).value().streamCodec(),
                SyncSavedDataPayload::value,
                SyncSavedDataPayload::untyped
        );
    }




}
