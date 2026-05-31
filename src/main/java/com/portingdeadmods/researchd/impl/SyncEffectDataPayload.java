package com.portingdeadmods.researchd.impl;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SyncEffectDataPayload(UUID teamId, ResearchEffectData<?> effectData) implements CustomPacketPayload {
    public static final Type<SyncEffectDataPayload> TYPE = new Type<>(Researchd.rl("sync_effect_data"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncEffectDataPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SyncEffectDataPayload::teamId,
            ResearchEffectData.STREAM_CODEC,
            SyncEffectDataPayload::effectData,
            SyncEffectDataPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamCache.teamResearchEffectDataMap.setEffectData(teamId, effectData);
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Failed to handle SyncEffectDataPayload", err);
           return null;
        });
    }

}
