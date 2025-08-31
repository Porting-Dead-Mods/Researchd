package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchQueueRemovePayload(ResearchInstance researchInstance) implements CustomPacketPayload {
    public static final Type<ResearchQueueRemovePayload> TYPE = new Type<>(Researchd.rl("research_queue_remove"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchQueueRemovePayload> STREAM_CODEC = StreamCodec.composite(
            ResearchInstance.STREAM_CODEC,
            ResearchQueueRemovePayload::researchInstance,
            ResearchQueueRemovePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void researchQueueRemoveAction(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Level level = serverPlayer.level();
                ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
                TeamResearchProgress researchProgress = data.getTeamByPlayer(serverPlayer).getResearchProgress();
                int index = researchProgress.researchQueue().getEntries().indexOf(researchInstance);
                researchProgress.researchQueue().remove(researchInstance);
                if (index == 0) {
                    researchProgress.researchQueue().setResearchProgress(0);
                }
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchQueueRemove payload", err);
            return null;
        });
    }

}
