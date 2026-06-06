package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DebugCommands {
    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("debug")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("teams-dump")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            source.sendSystemMessage(ResearchTeamHelperServer.getFormattedDump(source.getLevel()));
                            return 1;
                        }))
                .then(Commands.literal("data-dump")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            if (source.getPlayer() != null) {
                                displayPlayerPredicates(source.getPlayer());
                            }
                            return 1;
                        }))
                .build();
    }

    private static void displayPlayerPredicates(ServerPlayer player) {
        MutableComponent header = Component.literal("------ RESEARCHD PLAYER DATA ------")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        player.sendSystemMessage(header);

        MutableComponent playerInfo = Component.literal("Player: ")
                .withStyle(ChatFormatting.WHITE)
                .append(Component.literal(player.getName().getString())
                        .withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(playerInfo);

        ServerLevel level = player.serverLevel();
        ResearchTeam team = ResearchdApi.getTeamManager(level) == null ? null
                : ResearchdApi.getTeamManager(level).getTeamByPlayer(player);
        if (team == null) {
            player.sendSystemMessage(Component.literal("(no team)").withStyle(ChatFormatting.RED));
            return;
        }

        MutableComponent teamInfo = Component.literal("Team: ").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(team.getName()).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" (" + team.getId() + ")").withStyle(ChatFormatting.DARK_GRAY));
        player.sendSystemMessage(teamInfo);

        ResearchEffectManager effectManager = TeamResearchEffectSavedData.getData(level);
        for (ResearchEffectDataType<?> type : ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE) {
            ResearchEffectData<?> data = effectManager.getEffectData(team.getId(), type);
            String typeName = ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE.getKey(type).toString();
            if (data == null) {
                player.sendSystemMessage(Component.literal(typeName + ": (no entry)")
                        .withStyle(ChatFormatting.DARK_GRAY));
                continue;
            }

            MutableComponent effect = Component.literal(typeName).withStyle(ChatFormatting.GREEN);
            for (Object entry : data.getAll()) {
                effect.append(Component.literal("\n  - " + entry.toString()).withStyle(ChatFormatting.WHITE));
            }
            player.sendSystemMessage(effect);
        }

        MutableComponent footer = Component.literal("----------- END OF DATA -----------")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        player.sendSystemMessage(footer);
    }
}
