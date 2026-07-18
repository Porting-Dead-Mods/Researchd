package com.portingdeadmods.researchd.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ResearchdSuggestionUtils {
    public static CompletableFuture<Suggestions> teamMemberNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        List<String> members = new ArrayList<>();

        ServerPlayer player = context.getSource().getPlayer();
        ResearchTeam researchTeam = player != null ? ResearchTeamHelperServer.getTeamByMember(player) : null;
        if (researchTeam != null) {
            members.addAll(researchTeam.getMembers().stream()
                    .map(TeamMember::getName)
                    .toList());
        }

        members.add("none");
        return SharedSuggestionProvider.suggest(members, builder);
    }
}
