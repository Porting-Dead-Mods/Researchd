package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncEntityResearchPayload(EntityResearchImpl entityResearch) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEntityResearchPayload> STREAM_CODEC =
            EntityResearchImpl.STREAM_CODEC.map(SyncEntityResearchPayload::new, SyncEntityResearchPayload::entityResearch);
    public static final Type<SyncEntityResearchPayload> TYPE = new Type<>(Researchd.rl("sync_entity_research"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            EntityResearch capability = context.player().getCapability(ResearchdCapabilities.ENTITY);
            for (ResearchInstance r : entityResearch.researches()) {
                capability.addResearch(r);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });
    }
}
