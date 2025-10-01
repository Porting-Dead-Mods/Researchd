package com.portingdeadmods.researchd.networking.cache;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClearGraphCachePayload() implements CustomPacketPayload {
    public static final Type<ClearGraphCachePayload> TYPE = new Type<>(Researchd.rl("clear_graph_cache"));
    public static final ClearGraphCachePayload INSTANCE = new ClearGraphCachePayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearGraphCachePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void clearCache(ClearGraphCachePayload payload, IPayloadContext context) {
        context.enqueueWork(ResearchGraphCache::clearCache).exceptionally(e -> {
            context.disconnect(Component.literal("Failed to clear graph cache: " + e.getMessage()));
            return null;
        });
    }
}
