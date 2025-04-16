package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchFinishedPayload() implements CustomPacketPayload {
    public static final Type<ResearchFinishedPayload> TYPE = new Type<>(Researchd.rl("research_finished"));
    public static final ResearchFinishedPayload INSTANCE = new ResearchFinishedPayload();
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchFinishedPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            EntityResearchImpl data = ResearchdSavedData.PLAYER_RESEARCH.get().getData(player.level());
            ResearchInstance first = data.researchQueue().getEntries().getFirst();
            data.researchQueue().remove(0);
            data.completeResearch(first);
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload", err);
            return null;
        });
    }

}
