package com.portingdeadmods.researchd.impl;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.networking.PayloadSnapshots;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class SyncEffectDataPayload implements CustomPacketPayload {
    public static final Type<SyncEffectDataPayload> TYPE = new Type<>(Researchd.rl("sync_effect_data"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncEffectDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SyncEffectDataPayload::teamId,
                    ResearchEffectData.STREAM_CODEC,
                    SyncEffectDataPayload::effectData,
                    SyncEffectDataPayload::new);

    private final UUID teamId;
    private final ResearchEffectData<?> effectData;

    // Built only through snapshot(...), so no caller can hand the network thread live state
    private SyncEffectDataPayload(UUID teamId, ResearchEffectData<?> effectData) {
        this.teamId = teamId;
        this.effectData = effectData;
    }

    public UUID teamId() {
        return this.teamId;
    }

    public ResearchEffectData<?> effectData() {
        return this.effectData;
    }

    /** Builds the payload from a copy of the live effect data; see {@link PayloadSnapshots}. */
    public static SyncEffectDataPayload snapshot(UUID teamId, ResearchEffectData<?> effectData) {
        return new SyncEffectDataPayload(teamId, PayloadSnapshots.copy(ResearchEffectData.STREAM_CODEC, effectData));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
                    ResearchTeamCache.teamResearchEffectDataMap.setEffectData(teamId, effectData);
                })
                .exceptionally(err -> {
                    Researchd.LOGGER.error("Failed to handle SyncEffectDataPayload", err);
                    return null;
                });
    }
}
