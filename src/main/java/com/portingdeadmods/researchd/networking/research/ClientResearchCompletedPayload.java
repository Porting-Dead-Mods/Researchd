package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClientResearchCompletedPayload(ResourceKey<Research> key, int timeStamp, boolean forced) implements CustomPacketPayload {
    public static final Type<ClientResearchCompletedPayload> TYPE = new Type<>(Researchd.rl("research_finished"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ClientResearchCompletedPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY),
            ClientResearchCompletedPayload::key,
            ByteBufCodecs.INT,
            ClientResearchCompletedPayload::timeStamp,
            ByteBufCodecs.BOOL,
            ClientResearchCompletedPayload::forced,
            ClientResearchCompletedPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            @Nullable ResearchTeamManager data = ResearchdApi.getTeamManager(player.level());
            ResearchTeam team = data != null ? data.getTeamByPlayerId(player.getUUID()) : null;
            if (team == null) {
                context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.NO_RESEARCH_TEAM));
                return;
            }

            ResearchQueue queue = team.getQueue();
            ResourceKey<Research> first = null;

            if (!queue.isEmpty()) {
                first = queue.getFirst();
            }

            if (!forced && queue.isEmpty()) {
                context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.RESEARCH_QUEUE_DESYNC));
            }

            if (!forced && first != this.key()) {
                context.disconnect(ResearchdTranslations.component(ResearchdTranslations.Errors.RESEARCH_QUEUE_DESYNC));
            }

            team.setResearchCompleted(key, timeStamp);

            if (first != null) {
                queue.remove(0, false);
            }

            player.sendSystemMessage(
                    ResearchdTranslations.Research.QUEUE_FINISHED.component(
                            Researchd.MODID,
                            ChatFormatting.GREEN + Utils.registryTranslation(key).getString() + ChatFormatting.RESET,
                            ChatFormatting.GREEN + ResearchHelperCommon.getResearchCompletionTime(team.getCreationTime(), timeStamp()) + ChatFormatting.RESET
            ));

            ResearchTeamHelperClient.refreshResearchScreenData();
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload: ", err);
            return null;
        });
    }

}
