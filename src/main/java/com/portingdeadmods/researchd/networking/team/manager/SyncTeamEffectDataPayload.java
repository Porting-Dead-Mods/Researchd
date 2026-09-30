package com.portingdeadmods.researchd.networking.team.manager;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import com.portingdeadmods.researchd.networking.PayloadSnapshots;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class SyncTeamEffectDataPayload implements CustomPacketPayload {
    public static final Type<SyncTeamEffectDataPayload> TYPE =
            new Type<>(Researchd.rl("sync_team_research_effect_data"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncTeamEffectDataPayload> STREAM_CODEC =
            TeamResearchEffectDataMap.STREAM_CODEC.map(SyncTeamEffectDataPayload::new, SyncTeamEffectDataPayload::map);

    private final TeamResearchEffectDataMap map;

    // Built only through snapshot(...), so no caller can hand the network thread live state
    private SyncTeamEffectDataPayload(TeamResearchEffectDataMap map) {
        this.map = map;
    }

    public TeamResearchEffectDataMap map() {
        return this.map;
    }

    /** Builds the payload from a copy of the live map; see {@link PayloadSnapshots}. */
    public static SyncTeamEffectDataPayload snapshot(TeamResearchEffectDataMap map) {
        return new SyncTeamEffectDataPayload(PayloadSnapshots.copy(TeamResearchEffectDataMap.STREAM_CODEC, map));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
                    ResearchTeamCache.teamResearchEffectDataMap = map;
                })
                .exceptionally(err -> {
                    Researchd.LOGGER.error("Failed to handle SyncTeamEffectDataPayload", err);
                    context.disconnect(Component.literal("Failed to sync research team effect data"));
                    return null;
                });
    }
}
