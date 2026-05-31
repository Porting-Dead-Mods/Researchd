package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchReloadPayload() implements CustomPacketPayload {
    public static final ResearchReloadPayload INSTANCE = new ResearchReloadPayload();
    public static final Type<ResearchReloadPayload> TYPE = new Type<>(Researchd.rl("research_reload"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchReloadPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchHelperClient.reloadResearches(context.player().level());
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Encountered error while handling ResearchReloadPayload", err);
           return null;
        });
    }

}
