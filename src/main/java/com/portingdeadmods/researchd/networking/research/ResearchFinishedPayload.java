package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchFinishedPayload(int timeStamp) implements CustomPacketPayload {
    public static final Type<ResearchFinishedPayload> TYPE = new Type<>(Researchd.rl("research_finished"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchFinishedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ResearchFinishedPayload::timeStamp,
            ResearchFinishedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void researchFinishedAction(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ResearchTeam team = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level()).getTeamByMember(player.getUUID());
            ResearchQueue queue = team.getResearchProgress().researchQueue();

            if (!queue.isEmpty()) {
                ResearchInstance first = queue.getEntries().getFirst();
                first.setResearchStatus(ResearchStatus.RESEARCHED);
                queue.remove(0);
                UniqueArray<ResearchNode> children = ClientResearchCache.getNodeByResearch(ClientResearchCache.NODES, first.getResearch()).getChildren();
                for (ResearchNode child : children) {
                    child.getInstance().setResearchStatus(ResearchStatus.RESEARCHABLE);
                }
                team.getResearchProgress().completeResearch(first);

                player.sendSystemMessage(
                        ResearchdTranslations.Research.QUEUE_FINISHED.component(
                                Researchd.MODID,
                                ChatFormatting.GREEN + Utils.registryTranslation(first.getResearch()).getString() + ChatFormatting.RESET,
                                ChatFormatting.GREEN + team.getResearchCompletionTime(timeStamp()) + ChatFormatting.RESET
                        ));
            } else {
                context.disconnect(Component.translatable("researchd.error.research_queue_desync"));
            }
            if (Minecraft.getInstance().screen instanceof ResearchScreen screen)
                screen.getTechList().updateTechList();
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle ResearchFinishPayload", err);
            return null;
        });
    }

}
