package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.compat.KubeJSCompat;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchFinishedPayload(ResourceKey<Research> key, int timeStamp) implements CustomPacketPayload {
    public static final Type<ResearchFinishedPayload> TYPE = new Type<>(Researchd.rl("research_finished"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchFinishedPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY),
            ResearchFinishedPayload::key,
            ByteBufCodecs.INT,
            ResearchFinishedPayload::timeStamp,
            ResearchFinishedPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void researchFinishedAction(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level());
            ResearchTeam team = data.getTeamByMember(player.getUUID());
            if (team == null) {
                context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.NO_RESEARCH_TEAM));
                return;
            }

            ResearchQueue queue = team.getQueue();
            if (queue.isEmpty()) context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.RESEARCH_QUEUE_DESYNC));
            ResourceKey<Research> first = queue.getFirst();
            if (first != this.key()) context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.RESEARCH_QUEUE_DESYNC));

            team.completeResearch(first, timeStamp, player.level());
            queue.remove(0, false);

            if (player instanceof ServerPlayer serverPlayer) {
                KubeJSCompat.fireResearchCompletedEvent(serverPlayer, this.key());
            }

            player.sendSystemMessage(
                    ResearchdTranslations.Research.QUEUE_FINISHED.component(
                            Researchd.MODID,
                            ChatFormatting.GREEN + Utils.registryTranslation(first).getString() + ChatFormatting.RESET,
                            ChatFormatting.GREEN + ResearchHelperCommon.getResearchCompletionTime(team.getCreationTime(), timeStamp()) + ChatFormatting.RESET
            ));

            ClientResearchTeamHelper.refreshResearchScreenData();
            ResearchdSavedData.TEAM_RESEARCH.get().setData(player.level(), data);
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload: ", err);
            return null;
        });
    }

}
