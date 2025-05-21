package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import com.portingdeadmods.researchd.utils.UniqueArray;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
            ResearchQueue queue = data.researchQueue();
            if (!queue.isEmpty()) {
                ResearchInstance first = queue.getEntries().getFirst();
                first.setResearchStatus(ResearchStatus.RESEARCHED);
                queue.remove(0);
                UniqueArray<ResearchNode> children = ClientResearchCache.getNodeByResearch(first.getResearch()).getChildren();
                for (ResearchNode child : children) {
                    child.getInstance().setResearchStatus(ResearchStatus.RESEARCHABLE);
                }
                data.completeResearch(first);
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload", err);
            return null;
        });
    }

}
