package com.portingdeadmods.researchd.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ResearchdSuggestionUtils {
    public static CompletableFuture<Suggestions> teamMemberNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ResearchTeam researchTeam = ResearchTeamHelper.getTeamByMember(context.getSource().getPlayer());
        List<String> members = new ArrayList<>(researchTeam.getMembers().stream()
                .map(TeamMember::getName)
                .toList());
        members.add("none");
        return SharedSuggestionProvider.suggest(members, builder);
    }
}
