package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record ResearchQueueAddPayload(ResearchInstance researchInstance) implements CustomPacketPayload {
    public static final Type<ResearchQueueAddPayload> TYPE = new Type<>(Researchd.rl("research_queue_add"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchQueueAddPayload> STREAM_CODEC = StreamCodec.composite(
            ResearchInstance.STREAM_CODEC,
            ResearchQueueAddPayload::researchInstance,
            ResearchQueueAddPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void researchQueueAddAction(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Level level = serverPlayer.level();
                ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
                ResearchTeam team = data.getTeamByPlayer(serverPlayer);

                boolean added = team.getResearchProgress().researchQueue().add(researchInstance);
                if (!added) return;

                // Announce
                List<UUID> members = team.getMembers();
                Component researchName =  Utils.registryTranslation(researchInstance().getResearch());

                for (UUID memberId : members) {
                    ServerPlayer member = level.getServer().getPlayerList().getPlayer(memberId);
                    if (member != null) {
                        member.sendSystemMessage(Component.translatable(
                                "researchd.research.queue.added",
                                        ChatFormatting.GREEN + serverPlayer.getDisplayName().getString() + ChatFormatting.RESET,
                                ChatFormatting.GREEN + researchName.getString() + ChatFormatting.RESET
                        ));
                    }
                }

                ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchQueueAdd payload", err);
            return null;
        });
    }

}
