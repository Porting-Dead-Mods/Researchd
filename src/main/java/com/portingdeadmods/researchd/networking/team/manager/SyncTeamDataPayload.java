package com.portingdeadmods.researchd.networking.team.manager;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.networking.PayloadSnapshots;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class SyncTeamDataPayload implements CustomPacketPayload {
    public static final Type<SyncTeamDataPayload> TYPE = new Type<>(Researchd.rl("sync_team_data"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncTeamDataPayload> STREAM_CODEC =
            ResearchTeamMap.STREAM_CODEC.map(SyncTeamDataPayload::new, SyncTeamDataPayload::map);

    private final ResearchTeamMap map;

    // Built only through snapshot(...), so no caller can hand the network thread live state
    private SyncTeamDataPayload(ResearchTeamMap map) {
        this.map = map;
    }

    public ResearchTeamMap map() {
        return this.map;
    }

    /** Builds the payload from a copy of the live map; see {@link PayloadSnapshots}. */
    public static SyncTeamDataPayload snapshot(ResearchTeamMap map) {
        return new SyncTeamDataPayload(PayloadSnapshots.copy(ResearchTeamMap.STREAM_CODEC, map));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
                    ResearchTeamCache.researchTeamMap = map;
                })
                .exceptionally(err -> {
                    Researchd.LOGGER.error("Failed to handle SyncTeamDataPayload", err);
                    context.disconnect(Component.literal("Failed to sync research team data"));
                    return null;
                });
    }
}
