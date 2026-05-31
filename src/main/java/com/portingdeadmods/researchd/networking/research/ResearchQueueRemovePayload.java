package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.impl.team.TeamResearches;
import com.portingdeadmods.researchd.networking.client.RefreshResearchScreenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchQueueRemovePayload(ResourceKey<Research> researchKey) implements CustomPacketPayload {
    public static final Type<ResearchQueueRemovePayload> TYPE = new Type<>(Researchd.rl("research_queue_remove"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchQueueRemovePayload> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            ResearchQueueRemovePayload::researchKey,
            ResearchQueueRemovePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ServerLevel level = serverPlayer.serverLevel();
                ResearchTeamMap data = TeamSavedData.getData(level);

	            ResearchTeamImpl team = (ResearchTeamImpl) data.getTeamByPlayer(serverPlayer);
				if (team == null) return;

                TeamResearches teamResearches = team.getTeamResearches();
                teamResearches.researchQueue().remove(researchKey, true);

                ResearchQueueAddPayload.refreshResearches(team, level);
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchQueueRemove payload", err);
            return null;
        });
    }

}
