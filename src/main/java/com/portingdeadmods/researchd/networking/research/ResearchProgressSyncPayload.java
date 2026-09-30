package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.networking.PayloadSnapshots;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ResearchProgressSyncPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResearchProgressSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Researchd.rl("research_complete_progress_sync"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchProgressSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY),
                    ResearchProgressSyncPayload::key,
                    ResearchProgress.STREAM_CODEC,
                    ResearchProgressSyncPayload::progress,
                    ResearchProgressSyncPayload::new);

    private final ResourceKey<Research> key;
    private final ResearchProgress progress;

    // Built only through snapshot(...), so no caller can hand the network thread live state
    private ResearchProgressSyncPayload(ResourceKey<Research> key, ResearchProgress progress) {
        this.key = key;
        this.progress = progress;
    }

    public ResourceKey<Research> key() {
        return this.key;
    }

    public ResearchProgress progress() {
        return this.progress;
    }

    /** Builds the payload from a copy of the live progress; see {@link PayloadSnapshots}. */
    public static ResearchProgressSyncPayload snapshot(ResourceKey<Research> key, ResearchProgress progress) {
        return new ResearchProgressSyncPayload(key, PayloadSnapshots.copy(ResearchProgress.STREAM_CODEC, progress));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
                    ResearchTeam team = ResearchTeamHelperClient.getTeam();
                    if (team == null) return;

                    team.getResearchProgresses().put(this.key, this.progress);
                })
                .exceptionally(err -> {
                    Researchd.LOGGER.error("Failed to handle ResearchMethodProgressSyncPayload", err);
                    return null;
                });
    }
}
