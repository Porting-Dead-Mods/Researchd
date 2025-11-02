package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.impl.team.TeamResearches;
import com.portingdeadmods.researchd.networking.client.RefreshResearchScreenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
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
                Level level = serverPlayer.level();
                ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

	            SimpleResearchTeam team = data.getTeamByPlayer(serverPlayer);
				if (team == null) return;

                TeamResearches teamResearches = team.getTeamResearches();
                teamResearches.researchQueue().remove(researchKey, true);

                teamResearches.refreshResearchStatus();
                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
	            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);

	            for (TeamMember member : team.getMembers()) {
		            if (level.getPlayerByUUID(member.player()) == null) continue;

		            ServerPlayer player = (ServerPlayer) level.getPlayerByUUID(member.player());
		            PacketDistributor.sendToPlayer(player, RefreshResearchScreenData.ALL);
	            }
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchQueueRemove payload", err);
            return null;
        });
    }

}
