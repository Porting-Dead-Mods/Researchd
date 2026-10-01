package com.portingdeadmods.researchd.networking.team.manager;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.networking.PayloadSnapshots;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class SyncTeamPayload implements CustomPacketPayload {
    public static final Type<SyncTeamPayload> TYPE = new Type<>(Researchd.rl("sync_team"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncTeamPayload> STREAM_CODEC =
            ResearchTeamImpl.STREAM_CODEC.map(SyncTeamPayload::new, SyncTeamPayload::team);

    private final ResearchTeamImpl team;

    // Built only through snapshot(...), so no caller can hand the network thread live state
    private SyncTeamPayload(ResearchTeamImpl team) {
        this.team = team;
    }

    public ResearchTeamImpl team() {
        return this.team;
    }

    /** Builds the payload from a copy of the live team; see {@link PayloadSnapshots}. */
    public static SyncTeamPayload snapshot(ResearchTeamImpl team) {
        return new SyncTeamPayload(PayloadSnapshots.copy(ResearchTeamImpl.STREAM_CODEC, team));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
                    ResearchTeamCache.researchTeamMap.updateTeam(team);
                })
                .exceptionally(err -> {
                    Researchd.LOGGER.error("Failed to handle SyncTeamPayload", err);
                    return null;
                });
    }
}
