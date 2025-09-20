package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdCommonConfig;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.researchd.api.data.team.TeamMember;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record ResearchQueueAddPayload(ResourceKey<Research> researchKey, UUID player, long time) implements CustomPacketPayload {
    public static final Type<ResearchQueueAddPayload> TYPE = new Type<>(Researchd.rl("research_queue_add"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchQueueAddPayload> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            ResearchQueueAddPayload::researchKey,
            UUIDUtil.STREAM_CODEC,
            ResearchQueueAddPayload::player,
            ByteBufCodecs.VAR_LONG,
            ResearchQueueAddPayload::time,
            ResearchQueueAddPayload::new
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
                ResearchTeam team = data.getTeamByPlayer(serverPlayer);

				if (team.getResearchQueue().getEntries().size() >= ResearchdCommonConfig.researchQueueLength) return;

                ResearchInstance instance = team.getResearches().get(researchKey);
                instance.setResearchedPlayer(this.player);
                instance.setResearchedTime(this.time);

                boolean added = team.getResearchProgress().researchQueue().add(instance);
                if (!added) return;

                // Announce
                List<TeamMember> members = team.getMembers();
                Component researchName = Utils.registryTranslation(this.researchKey);

                for (TeamMember memberId : members) {
                    ServerPlayer member = level.getServer().getPlayerList().getPlayer(memberId.player());
                    if (member != null) {
                        member.sendSystemMessage(ResearchdTranslations.Research.QUEUE_ADDED.component(
                                Researchd.MODID,
                                ChatFormatting.GREEN + serverPlayer.getDisplayName().getString() + ChatFormatting.RESET,
                                ChatFormatting.GREEN + researchName.getString() + ChatFormatting.RESET
                        ));
                    }
                }

                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
                team.getResearchProgress().refreshResearchStatus(level);
	            ResearchdSavedData.TEAM_RESEARCH.get().sync(level);
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchQueueAdd payload", err);
            return null;
        });
    }

}
