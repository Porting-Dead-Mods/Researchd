package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchCacheReloadPayload() implements CustomPacketPayload {
    public static final ResearchCacheReloadPayload INSTANCE = new ResearchCacheReloadPayload();
    public static final Type<ResearchCacheReloadPayload> TYPE = new Type<>(Researchd.rl("research_cache_reload"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchCacheReloadPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchHelperClient.reloadResearches(context.player().level());
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Encountered error while handling ResearchCacheReloadPayload", err);
           return null;
        });
    }

}
